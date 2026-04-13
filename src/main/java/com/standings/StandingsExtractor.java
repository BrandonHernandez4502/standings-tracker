package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Properties;

public class StandingsExtractor {
    private static final Logger logger = LoggerFactory.getLogger(StandingsExtractor.class);
    private static final Properties PROPS = loadProperties();
    private static final String API_TOKEN = PROPS.getProperty("api.token");
    private static final String BASE_URL   = PROPS.getProperty("api.base.url");
    private static final String LEAGUE     = PROPS.getProperty("api.league.code");

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = StandingsExtractor.class
                .getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Could not load application.properties", e);
        }
        return props;
    }

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        logger.info("ETL job started");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/competitions/" + LEAGUE + "/standings"))
            .header("X-Auth-Token", API_TOKEN)
            .GET()
            .build();

        HttpResponse<String> response = fetchWithRetry(client, request);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body());

        StandingsTransformer transformer = new StandingsTransformer();
        List<StandingSnapshot> snapshots = transformer.transform(root, LEAGUE);

        StandingsRepository repository = new StandingsRepository();
        repository.insertAll(snapshots);

        for (StandingSnapshot s : snapshots) {
            logger.info(String.format("%2d. %-30s Pts:%-4d P:%d W:%d D:%d L:%d",
                    s.position(), s.teamName(), s.points(),
                    s.played(), s.won(), s.drawn(), s.lost()));
        }

        logger.info("ETL job complete");
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
