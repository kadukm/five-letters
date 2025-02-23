package org.example.fiveletters.solving.common.engine.service.nextword;

import java.util.List;
import java.util.Set;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.service.filtering.FilteringStrategy;
import org.example.fiveletters.solving.common.engine.service.filtering.OneActionFilteringService;
import org.slf4j.event.Level;

public class OptimalNextWordSearcher {

    private final FilteringStrategy filteringStrategy;
    private final NextWordSearchStrategy nextWordSearchStrategy;

    public OptimalNextWordSearcher(FilteringStrategy filteringStrategy, NextWordSearchStrategy nextWordSearchStrategy) {
        this.filteringStrategy = filteringStrategy;
        this.nextWordSearchStrategy = nextWordSearchStrategy;
    }

    public Word findNextWord(Set<Word> allWords, Set<Word> possibleAnswers) {
        Set<Word> wordsToFilter = switch (nextWordSearchStrategy) {
            case ALL_WORDS -> allWords;
            case ANSWERS -> possibleAnswers;
        };
        List<Action> actions = getActionsToFilter(wordsToFilter);

        FilteringResult filteringResult =
            new OneActionFilteringService(possibleAnswers, actions, Level.DEBUG, filteringStrategy).filterActions();

        return filteringResult.action().getWords().stream().findFirst().orElseThrow();
    }

    private List<Action> getActionsToFilter(Set<Word> words) {
        return words.stream()
            .map(w -> new Action(Set.of(w)))
            .toList();
    }
}
