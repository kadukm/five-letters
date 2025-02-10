package org.example.fiveletters.solving.beginningsearch.util.dto;

import java.math.BigDecimal;

public record FilteringResult(
    Beginning beginning,
    BigDecimal averageRemainingAnswersCount,
    int maxRemainingAnswersCount
) {}
