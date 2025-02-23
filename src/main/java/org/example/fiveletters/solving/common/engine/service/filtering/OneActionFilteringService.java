package org.example.fiveletters.solving.common.engine.service.filtering;

import java.util.List;
import java.util.Set;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.slf4j.event.Level;

public class OneActionFilteringService extends AbstractActionFilteringService {

    public OneActionFilteringService(Set<Word> possibleAnswers, List<Action> actions,
                                     Level logLevel, FilteringStrategy filteringStrategy) {
        super(possibleAnswers, actions, logLevel, filteringStrategy);
    }

    public FilteringResult filterActions() {
        return handleActions()
            .min(createComparator())
            .map(this::mapInternalFilteringResult)
            .orElseThrow();
    }
}
