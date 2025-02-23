package org.example.fiveletters.solving.beginningsearch;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.beginningsearch.service.BruteForceUniqueBeginningProducer;
import org.example.fiveletters.solving.beginningsearch.service.FirstWordBeginningProducer;
import org.example.fiveletters.solving.beginningsearch.service.LetterFrequencyUniqueBeginningProducer;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.engine.service.filtering.FilteringStrategy;
import org.example.fiveletters.solving.common.engine.service.filtering.ManyActionsFilteringService;
import org.example.fiveletters.solving.common.util.DictionariesChecker;
import org.slf4j.event.Level;

@Slf4j
public class BeginningSearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read("dictionaries/all-words.csv", WordSource.OPEN_CORPORA);
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        DictionariesChecker.check(allWordsDictionary, answersDictionary);

//        List<Action> beginnings = createCustomBeginning("океан");
        List<Action> beginnings = FirstWordBeginningProducer.produce(allWordsDictionary);
//        List<Action> beginnings = BruteForceUniqueBeginningProducer.produce(allWordsDictionary, 4);
//        List<Action> beginnings = LetterFrequencyUniqueBeginningProducer.produce(allWordsDictionary, answersDictionary, 4);

        log.info("Found beginnings: {}", beginnings.size());

        if (beginnings.isEmpty()) {
            log.warn("Cannot find beginning");
            return;
        }

        List<FilteringResult> filteringResults = new ManyActionsFilteringService(
            answersDictionary.getWords(), beginnings, Level.INFO, FilteringStrategy.AVERAGE
        ).filterActions(10);

        log.info(formatLogMessage(filteringResults));

//        findBeginningPosition(filteringResults, "океан");
    }

    private static List<Action> createCustomBeginning(String value) {
        return Optional.of(value)
            .map(Word::new)
            .map(Set::of)
            .map(Action::new)
            .map(List::of)
            .orElseThrow();
    }

    private static void findBeginningPosition(List<FilteringResult> filteringResults, String... values) {
        Set<Word> wordsToFind = Stream.of(values)
            .map(Word::new)
            .collect(Collectors.toSet());

        for (int i = 0; i < filteringResults.size(); i++) {
            FilteringResult filteringResult = filteringResults.get(i);
            Set<Word> currentWords = filteringResult.action().getWords();
            if (currentWords.equals(wordsToFind)) {
                log.info("Beginning {} takes {} place of {}", wordsToFind, i + 1, filteringResults.size());
                return;
            }
        }

        log.info("Beginning {} not found in top-{}", wordsToFind, filteringResults.size());
    }

    private static String formatLogMessage(List<FilteringResult> filteringResults) {
        StringBuilder sb = new StringBuilder();

        sb.append("Found best beginnings:");

        for (int i = 0; i < filteringResults.size(); i++) {
            FilteringResult filteringResult = filteringResults.get(i);

            sb.append('\n').append(i + 1).append(". ").append(filteringResult.action().getWords())
                .append("\n  average remaining answers count: ").append(filteringResult.averageRemainingAnswersCount())
                .append("\n  max remaining answers count: ").append(filteringResult.maxRemainingAnswersCount());
        }

        return sb.toString();
    }
}
