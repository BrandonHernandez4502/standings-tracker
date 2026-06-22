package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class StandingsExtractor {
    private static final Logger logger = LoggerFactory.getLogger(StandingsExtractor.class);
    private static final Properties PROPS = loadProperties();
    private static final String API_TOKEN        = PROPS.getProperty("api.token");
    private static final String BASE_URL         = PROPS.getProperty("api.base.url");
    private static final List<String> COMPETITIONS = Arrays.stream(
            PROPS.getProperty("api.competition.codes", "PL").split(","))
            .map(String::trim)
            .collect(Collectors.toList());

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = StandingsExtractor.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (in != null) props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        String[] keys = {
            "api.token", "api.base.url", "api.competition.codes",
            "db.url", "db.user", "db.password", "scheduler.cron"
        };
        for (String key : keys) {
            String envVal = System.getenv(key.toUpperCase().replace('.', '_'));
            if (envVal != null) props.setProperty(key, envVal);
        }
        return props;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            LocalDate from = LocalDate.parse(args[0]);
            LocalDate to   = LocalDate.parse(args[1]);
            runBackfill(from, to);
        } else {
            run();
        }
    }

    public static void run() throws Exception {
        logger.info("ETL job started — competitions: {}", COMPETITIONS);
        for (String code : COMPETITIONS) {
            runCompetition(code, LocalDate.now());
        }
        logger.info("ETL job complete");
    }

    public static void runBackfill(LocalDate from, LocalDate to) throws Exception {
        logger.info("Backfill started: {} to {}", from, to);
        for (LocalDate date = from; !date.isAfter(to); date = date.plusDays(1)) {
            for (String code : COMPETITIONS) {
                runCompetition(code, date);
                Thread.sleep(7_000); // stay under 10 calls/min rate limit
            }
        }
        logger.info("Backfill complete");
    }

    private static void runCompetition(String competitionCode, LocalDate date) throws Exception {
        logger.info("Fetching standings for {} on {}", competitionCode, date);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/competitions/" + competitionCode + "/standings?date=" + date))
            .header("X-Auth-Token", API_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = fetchWithRetry(client, request);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        StandingsTransformer transformer = new StandingsTransformer();
        List<StandingSnapshot> snapshots = transformer.transform(root, competitionCode, date);

        StandingsRepository repository = new StandingsRepository();
        repository.insertAll(snapshots);

        for (StandingSnapshot s : snapshots) {
            logger.info(String.format("[%s] %s %2d. %-30s Pts:%-4d P:%d W:%d D:%d L:%d",
                    competitionCode,
                    s.groupName().isEmpty() ? "    " : s.groupName(),
                    s.position(), s.teamName(), s.points(),
                    s.played(), s.won(), s.drawn(), s.lost()));
        }
    }

    private static HttpResponse<String> fetchWithRetry(HttpClient client, HttpRequest request) throws Exception {
        int maxAttempts = 3;
        int waitSeconds = 30;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    logger.info("API response status: {}", response.statusCode());
                    return response;
                }

                logger.warn("Attempt {}/{} — unexpected status code: {}", attempt, maxAttempts, response.statusCode());
            } catch (Exception e) {
                logger.warn("Attempt {}/{} — request failed: {}", attempt, maxAttempts, e.getMessage());
            }

            if (attempt < maxAttempts) {
                logger.info("Waiting {} seconds before retrying...", waitSeconds);
                Thread.sleep(waitSeconds * 1000L);
            }
        }

        throw new RuntimeException("API call failed after " + maxAttempts + " attempts");
    }
}
