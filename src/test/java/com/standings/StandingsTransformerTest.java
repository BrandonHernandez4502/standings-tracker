package com.standings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StandingsTransformerTest {

    private StandingsTransformer transformer;
    private JsonNode fakeApiResponse;

    // Runs before each test — sets up the transformer and a fake API response
    @BeforeEach
    void setUp() throws Exception {
        transformer = new StandingsTransformer();

        String json = """
                {
                  "standings": [
                    {
                      "table": [
                        {
                          "position": 1,
                          "team": { "id": 57, "name": "Arsenal FC" },
                          "playedGames": 30,
                          "won": 21,
                          "draw": 5,
                          "lost": 4,
                          "goalsFor": 68,
                          "goalsAgainst": 29,
                          "goalDifference": 39,
                          "points": 68,
                          "form": "W,W,D,W,L"
                        },
                        {
                          "position": 2,
                          "team": { "id": 65, "name": "Manchester City FC" },
                          "playedGames": 30,
                          "won": 19,
                          "draw": 4,
                          "lost": 7,
                          "goalsFor": 60,
                          "goalsAgainst": 35,
                          "goalDifference": 25,
                          "points": 61,
                          "form": "W,L,W,W,D"
                        }
                      ]
                    }
                  ]
                }
                """;

        fakeApiResponse = new ObjectMapper().readTree(json);
    }

    @Test
    void transformReturnsTwoSnapshots() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals(2, snapshots.size());
    }

    @Test
    void transformMapsTeamNameCorrectly() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals("Arsenal FC", snapshots.get(0).teamName());
    }

    @Test
    void transformMapsPositionCorrectly() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals(1, snapshots.get(0).position());
        assertEquals(2, snapshots.get(1).position());
    }

    @Test
    void transformMapsPointsCorrectly() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals(68, snapshots.get(0).points());
    }

    @Test
    void transformSetsLeagueId() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals("PL", snapshots.get(0).leagueId());
    }

    @Test
    void transformSetsSnapshotDateToToday() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals(LocalDate.now(), snapshots.get(0).snapshotDate());
    }

    @Test
    void transformMapsFormCorrectly() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals("W,W,D,W,L", snapshots.get(0).form());
    }

    @Test
    void transformMapsGoalDifferenceCorrectly() {
        List<StandingSnapshot> snapshots = transformer.transform(fakeApiResponse, "PL");
        assertEquals(39, snapshots.get(0).goalDifference());
    }
}
