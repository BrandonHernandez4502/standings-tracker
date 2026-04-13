package com.standings;

import java.time.LocalDate;

public record StandingSnapshot (
    String leagueId,
    int teamId,
    String teamName,
    LocalDate snapshotDate,
    int position,
    int played,
    int won,
    int drawn,
    int lost,
    int goalsFor,
    int goalsAgainst,
    int goalDifference,
    int points,
    String form
 ) {}