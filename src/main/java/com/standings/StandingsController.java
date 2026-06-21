package com.standings;

import org.springframework.web.bind.annotation.*;

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
}
