package org.example.fiveletters.solving.engine.dto;

import java.math.BigDecimal;

public record FilteringResult(
    Action action,
    BigDecimal averageRemainingAnswersCount,
    int maxRemainingAnswersCount
) {}
