package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class StandingsController {

    private static final Logger logger = LoggerFactory.getLogger(StandingsController.class);
    private static final Set<String> WC_CODES = Set.of("WC", "WC2026", "WC26");

    private final StandingsRepository repository;
    // Cached once — group assignments don't change during a tournament
    private final ConcurrentHashMap<String, Map<Integer, String>> teamGroupCache = new ConcurrentHashMap<>();

    public StandingsController(StandingsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/competitions")
    public List<String> getCompetitions() throws Exception {
        return repository.findCompetitions();
    }

    @GetMapping("/standings/{competition}")
    public List<StandingSnapshot> getLatestStandings(
            @PathVariable("competition") String competition,
            @RequestParam(value = "season", required = false) Integer season) throws Exception {
        String code = competition.toUpperCase();
        int resolvedSeason = (season != null) ? season : repository.findLatestSeason(code);
        List<StandingSnapshot> rows = repository.findLatest(code, resolvedSeason);

        // WC only: the standings API returns no group info, so we annotate from the matches API
        if (WC_CODES.contains(code) && rows.stream().anyMatch(r -> r.groupName().isEmpty())) {
            rows = annotateWithGroups(code, rows);
        }
        return rows;
    }

    private List<StandingSnapshot> annotateWithGroups(String code, List<StandingSnapshot> rows) {
        Map<Integer, String> groups = teamGroupCache.get(code);
        if (groups == null) {
            groups = fetchTeamGroups(code);
            if (!groups.isEmpty()) teamGroupCache.put(code, groups);
        }
        if (groups.isEmpty()) return rows;

        final Map<Integer, String> finalGroups = groups;
        // Assign group names and re-sort: by group label, then by points/GD/GF within each group
        return rows.stream()
            .filter(r -> finalGroups.containsKey(r.teamId()))
            .collect(Collectors.groupingBy(r -> finalGroups.get(r.teamId()), LinkedHashMap::new, Collectors.toList()))
            .entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .flatMap(entry -> {
                String groupName = entry.getKey();
                List<StandingSnapshot> groupRows = entry.getValue().stream()
                    .sorted(Comparator.comparingInt(StandingSnapshot::points).reversed()
                        .thenComparingInt(StandingSnapshot::goalDifference).reversed()
                        .thenComparingInt(StandingSnapshot::goalsFor).reversed())
                    .collect(Collectors.toList());
                List<StandingSnapshot> reranked = new ArrayList<>();
                for (int i = 0; i < groupRows.size(); i++) {
                    StandingSnapshot s = groupRows.get(i);
                    reranked.add(new StandingSnapshot(
                        s.leagueId(), s.season(), groupName, s.teamId(), s.teamName(),
                        s.snapshotDate(), i + 1,
                        s.played(), s.won(), s.drawn(), s.lost(),
                        s.goalsFor(), s.goalsAgainst(), s.goalDifference(), s.points(), s.form()
                    ));
                }
                return reranked.stream();
            })
            .collect(Collectors.toList());
    }

    private Map<Integer, String> fetchTeamGroups(String competitionCode) {
        try {
            JsonNode root = StandingsExtractor.fetchCompetitionJson(
                "/competitions/" + competitionCode + "/matches?stage=GROUP_STAGE"
            );
            Map<Integer, String> groups = new HashMap<>();
            for (JsonNode m : root.path("matches")) {
                JsonNode groupNode = m.path("group");
                if (groupNode.isNull() || groupNode.isMissingNode()) continue;
                String group = groupNode.asText("");
                if (group.isEmpty()) continue;
                int homeId = m.path("homeTeam").path("id").asInt();
                int awayId = m.path("awayTeam").path("id").asInt();
                if (homeId > 0) groups.put(homeId, group);
                if (awayId > 0) groups.put(awayId, group);
            }
            return groups;
        } catch (Exception e) {
            logger.warn("Could not fetch group assignments for {}: {}", competitionCode, e.getMessage());
            return Map.of();
        }
    }

    @GetMapping("/seasons/{competition}")
    public List<Integer> getSeasons(@PathVariable("competition") String competition) throws Exception {
        return repository.findSeasons(competition.toUpperCase());
    }

    private static final List<String> KNOCKOUT_STAGES = List.of(
        "LAST_32", "LAST_16", "QUARTER_FINALS", "SEMI_FINALS", "THIRD_PLACE", "FINAL"
    );
    private static final Map<String, String> STAGE_LABELS = Map.of(
        "LAST_32",        "Round of 32",
        "LAST_16",        "Round of 16",
        "QUARTER_FINALS", "Quarter-finals",
        "SEMI_FINALS",    "Semi-finals",
        "THIRD_PLACE",    "3rd Place",
        "FINAL",          "Final"
    );

    private final ConcurrentHashMap<String, List<BracketRound>> bracketCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> bracketCacheTime = new ConcurrentHashMap<>();
    private static final long BRACKET_CACHE_TTL_MS = 60_000;

    @GetMapping("/bracket/{competition}")
    public List<BracketRound> getBracket(@PathVariable("competition") String competition) throws Exception {
        String code = competition.toUpperCase();

        Long lastFetch = bracketCacheTime.get(code);
        if (lastFetch != null && System.currentTimeMillis() - lastFetch < BRACKET_CACHE_TTL_MS) {
            return bracketCache.get(code);
        }

        JsonNode root = StandingsExtractor.fetchCompetitionJson("/competitions/" + code + "/matches");

        // Collect matches grouped by stage, preserving KNOCKOUT_STAGES order
        Map<String, List<BracketMatch>> byStage = new LinkedHashMap<>();
        KNOCKOUT_STAGES.forEach(s -> byStage.put(s, new ArrayList<>()));

        for (JsonNode m : root.path("matches")) {
            String stage = m.path("stage").asText();
            if (!byStage.containsKey(stage)) continue;
            int matchId = m.path("id").asInt();
            String homeTeam = m.path("homeTeam").path("name").asText("");
            String awayTeam = m.path("awayTeam").path("name").asText("");
            if (homeTeam.isEmpty()) homeTeam = "TBD";
            if (awayTeam.isEmpty()) awayTeam = "TBD";
            JsonNode fullTime = m.path("score").path("fullTime");
            JsonNode homeNode = fullTime.path("home");
            JsonNode awayNode = fullTime.path("away");
            Integer homeScore = (homeNode.isNull() || homeNode.isMissingNode()) ? null : homeNode.asInt();
            Integer awayScore = (awayNode.isNull() || awayNode.isMissingNode()) ? null : awayNode.asInt();
            String status = m.path("status").asText();
            JsonNode winnerNode = m.path("score").path("winner");
            String winner = (winnerNode.isNull() || winnerNode.isMissingNode()) ? null : winnerNode.asText();
            String utcDate = m.path("utcDate").asText(null);
            byStage.get(stage).add(new BracketMatch(matchId, homeTeam, awayTeam, homeScore, awayScore, status, winner, utcDate));
        }

        // Sort each stage by API match ID — IDs reflect official bracket position, not match date
        byStage.values().forEach(list -> list.sort(Comparator.comparingInt(BracketMatch::matchNumber)));

        List<BracketRound> rounds = new ArrayList<>();
        byStage.forEach((stage, matches) -> {
            if (!matches.isEmpty()) {
                rounds.add(new BracketRound(stage, STAGE_LABELS.getOrDefault(stage, stage), matches));
            }
        });

        bracketCache.put(code, rounds);
        bracketCacheTime.put(code, System.currentTimeMillis());
        return rounds;
    }
}
