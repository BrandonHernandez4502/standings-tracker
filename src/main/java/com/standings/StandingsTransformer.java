package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandingsTransformer {
    
    public List<StandingSnapshot> transform(JsonNode root, String leagueId) {

        List<StandingSnapshot> snapshots = new ArrayList<>();

        JsonNode table = root
                .path("standings")
                .get(0)
                .path("table");
        
                for (JsonNode entry : table) {

            int teamId          = entry.path("team").path("id").asInt();
            String teamName     = entry.path("team").path("name").asText();
            int position        = entry.path("position").asInt();
            int played          = entry.path("playedGames").asInt();
            int won             = entry.path("won").asInt();
            int drawn           = entry.path("draw").asInt();
            int lost            = entry.path("lost").asInt();
            int goalsFor        = entry.path("goalsFor").asInt();
            int goalsAgainst    = entry.path("goalsAgainst").asInt();
            int goalDifference  = entry.path("goalDifference").asInt();
            int points          = entry.path("points").asInt();
            String form         = entry.path("form").asText(null); // null for if form is missing

            StandingSnapshot snapshot = new StandingSnapshot(
                    leagueId,
                    teamId,
                    teamName,
                    LocalDate.now(),
                    position,
                    played,
                    won,
                    drawn,
                    lost,
                    goalsFor,
                    goalsAgainst,
                    goalDifference,
                    points,
                    form
            );

            snapshots.add(snapshot);
        }
        return snapshots;
    }
}
