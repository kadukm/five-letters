package org.example.fiveletters.solving.research.dto;

import java.math.BigDecimal;
import java.util.SortedMap;
import org.example.fiveletters.solving.common.engine.dto.Action;

public record SummaryStats(
    Action beginning,
    BigDecimal averageStepsSpentCount,
    int lostGamesCount,
    SortedMap<Integer, Integer> stepsCountStats
) { }
