package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class StandingsController {

    private final StandingsRepository repository;

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
        return repository.findLatest(code, resolvedSeason);
    }

    @GetMapping("/seasons/{competition}")
    public List<Integer> getSeasons(@PathVariable("competition") String competition) throws Exception {
        return repository.findSeasons(competition.toUpperCase());
    }

    @GetMapping("/bracket/{competition}")
    public List<BracketMatch> getBracket(@PathVariable("competition") String competition) throws Exception {
        String code = competition.toUpperCase();
        JsonNode root = StandingsExtractor.fetchCompetitionJson(
            "/competitions/" + code + "/matches?stage=LAST_32"
        );
        List<BracketMatch> matches = new ArrayList<>();
        int num = 1;
        for (JsonNode m : root.path("matches")) {
            String homeTeam = m.path("homeTeam").path("name").asText();
            String awayTeam = m.path("awayTeam").path("name").asText();
            JsonNode fullTime = m.path("score").path("fullTime");
            JsonNode homeNode = fullTime.path("home");
            JsonNode awayNode = fullTime.path("away");
            Integer homeScore = (homeNode.isNull() || homeNode.isMissingNode()) ? null : homeNode.asInt();
            Integer awayScore = (awayNode.isNull() || awayNode.isMissingNode()) ? null : awayNode.asInt();
            String status = m.path("status").asText();
            JsonNode winnerNode = m.path("score").path("winner");
            String winner = (winnerNode.isNull() || winnerNode.isMissingNode()) ? null : winnerNode.asText();
            String utcDate = m.path("utcDate").asText(null);
            matches.add(new BracketMatch(num++, homeTeam, awayTeam, homeScore, awayScore, status, winner, utcDate));
        }
        return matches;
    }
}
