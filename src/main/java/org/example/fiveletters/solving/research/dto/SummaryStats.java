package org.example.fiveletters.solving.research.dto;

import java.math.BigDecimal;
import java.util.SortedMap;

public record SummaryStats(
    BigDecimal averageStepsSpentCount,
    int lostGamesCount,
    SortedMap<Integer, Integer> stepsCountStats
) { }
