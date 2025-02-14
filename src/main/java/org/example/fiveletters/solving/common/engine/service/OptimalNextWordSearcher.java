package org.example.fiveletters.solving.common.engine.service;

import java.util.List;
import java.util.Set;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.domain.Word;

public class OptimalNextWordSearcher {

    public Word findNextWord(List<Word> words, List<Word> possibleAnswers) {
        List<Action> actions = words.stream()
            .map(w -> new Action(Set.of(w)))
            .toList();

        FilteringResult filteringResult = new ActionFilteringService(possibleAnswers, actions).filterActions();

        return filteringResult.action().getWords().stream().findFirst().orElseThrow();
    }
}
