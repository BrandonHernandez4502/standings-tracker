package com.standings;

public record BracketMatch(
    int matchNumber,
    String homeTeam,
    String awayTeam,
    Integer homeScore,
    Integer awayScore,
    String status,
    String winner,
    String utcDate
) {}
