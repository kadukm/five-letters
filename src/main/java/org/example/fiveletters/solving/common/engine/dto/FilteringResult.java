package org.example.fiveletters.solving.common.engine.dto;

import java.math.BigDecimal;
import org.example.fiveletters.solving.common.domain.Word;

public record FilteringResult(
    Action action,
    BigDecimal averageRemainingAnswersCount,
    int maxRemainingAnswersCount
) {
    public Word getFirstWord() {
        return action.getWords().getFirst();
    }
}
