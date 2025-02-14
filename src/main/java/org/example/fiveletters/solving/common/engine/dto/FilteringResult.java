package org.example.fiveletters.solving.common.engine.dto;

import java.math.BigDecimal;

public record FilteringResult(
    Action action,
    BigDecimal averageRemainingAnswersCount,
    int maxRemainingAnswersCount
) {}
