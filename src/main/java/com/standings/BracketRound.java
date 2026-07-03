package com.standings;

import java.util.List;

public record BracketRound(String stage, String label, List<BracketMatch> matches) {}
