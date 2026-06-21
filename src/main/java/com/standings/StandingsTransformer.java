package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandingsTransformer {
    
    public List<StandingSnapshot> transform(JsonNode root, String leagueId, LocalDate date) {

        String startDate = root.path("season").path("startDate").asText("");
        int season = startDate.isEmpty() ? date.getYear() : Integer.parseInt(startDate.substring(0, 4));

        List<StandingSnapshot> snapshots = new ArrayList<>();

        for (JsonNode standingGroup : root.path("standings")) {
            JsonNode groupNode = standingGroup.path("group");
            // asText("") alone doesn't work here: NullNode.asText("") returns "null", not ""
            String groupName = (groupNode.isNull() || groupNode.isMissingNode()) ? "" : groupNode.asText();

            for (JsonNode entry : standingGroup.path("table")) {

                int teamId         = entry.path("team").path("id").asInt();
                String teamName    = entry.path("team").path("name").asText();
                int position       = entry.path("position").asInt();
                int played         = entry.path("playedGames").asInt();
                int won            = entry.path("won").asInt();
                int drawn          = entry.path("draw").asInt();
                int lost           = entry.path("lost").asInt();
                int goalsFor       = entry.path("goalsFor").asInt();
                int goalsAgainst   = entry.path("goalsAgainst").asInt();
                int goalDifference = entry.path("goalDifference").asInt();
                int points         = entry.path("points").asInt();
                String form        = entry.path("form").asText(null);

                snapshots.add(new StandingSnapshot(
                        leagueId,
                        season,
                        groupName,
                        teamId,
                        teamName,
                        date,
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
                ));
            }
        }
        return snapshots;
    }
}
