package org.example.fiveletters.solving.common.engine.service.filtering;

import java.util.List;
import java.util.Set;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.slf4j.event.Level;

public class ManyActionsFilteringService extends AbstractActionFilteringService {

    public ManyActionsFilteringService(Set<Word> possibleAnswers, List<Action> actions, Level logLevel) {
        super(possibleAnswers, actions, logLevel);
    }

    public List<FilteringResult> filterActions(int limit) {
        return handleActions()
            .sorted(createComparatorByRemainingAnswersSum())
            .limit(limit)
            .map(this::mapInternalFilteringResult)
            .toList();
    }
}
