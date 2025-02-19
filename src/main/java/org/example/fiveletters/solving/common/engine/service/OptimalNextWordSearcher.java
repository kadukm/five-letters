package org.example.fiveletters.solving.common.engine.service;

import java.util.List;
import java.util.Set;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.service.filtering.OneActionFilteringService;
import org.slf4j.event.Level;

public class OptimalNextWordSearcher {

    public Word findNextWord(Set<Word> words, Set<Word> possibleAnswers) {
        List<Action> actions = words.stream()
            .map(w -> new Action(Set.of(w)))
            .toList();

        FilteringResult filteringResult =
            new OneActionFilteringService(possibleAnswers, actions, Level.DEBUG).filterActions();

        return filteringResult.action().getWords().stream().findFirst().orElseThrow();
    }
}
