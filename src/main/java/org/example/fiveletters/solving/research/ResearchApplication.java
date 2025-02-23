package org.example.fiveletters.solving.research;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.beginningsearch.service.FirstWordBeginningProducer;
import org.example.fiveletters.solving.beginningsearch.service.LetterFrequencyUniqueBeginningProducer;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.service.filtering.FilteringStrategy;
import org.example.fiveletters.solving.common.engine.service.filtering.OneActionFilteringService;
import org.example.fiveletters.solving.common.engine.service.nextword.NextWordSearchStrategy;
import org.example.fiveletters.solving.common.util.DictionariesChecker;
import org.example.fiveletters.solving.research.dto.SummaryStats;
import org.example.fiveletters.solving.research.service.StatsCalculator;
import org.slf4j.event.Level;

@Slf4j
public class ResearchApplication {

    private static final FilteringStrategy FILTERING_STRATEGY = FilteringStrategy.AVERAGE;

    private static final NextWordSearchStrategy NEXT_WORD_SEARCH_STRATEGY = NextWordSearchStrategy.ALL_WORDS;

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read("dictionaries/all-words.csv", WordSource.OPEN_CORPORA);
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        DictionariesChecker.check(allWordsDictionary, answersDictionary);

        Action beginning = createCustomBeginning("норка");
//        Action beginning = createOptimalOneWordBeginning(allWordsDictionary, answersDictionary);
//        Action beginning = createOptimalFewWordsBeginning(allWordsDictionary, answersDictionary, 4);

        log.info("Using beginning {}", beginning.getWords());

        SummaryStats summary = StatsCalculator
            .calculate(allWordsDictionary, answersDictionary, beginning, FILTERING_STRATEGY, NEXT_WORD_SEARCH_STRATEGY);

        log.info(
            """
            Research statistics:
            beginning: {}
            steps stats: {}
            average steps spent: {}
            lost games count: {}
            """,
            beginning.getWords(),
            formatStepsStatsString(summary.stepsCountStats()),
            summary.averageStepsSpentCount(),
            summary.lostGamesCount()
        );
    }

    private static Action createCustomBeginning(String ... values) {
        Set<Word> words = Stream.of(values)
            .map(Word::new)
            .collect(Collectors.toSet());

        return new Action(words);
    }

    private static Action createOptimalOneWordBeginning(Dictionary allWordsDictionary, Dictionary answersDictionary) {
        log.info("Searching optimal one word beginning");

        List<Action> beginnings = switch (NEXT_WORD_SEARCH_STRATEGY) {
            case ALL_WORDS ->  FirstWordBeginningProducer.produce(allWordsDictionary);
            case ANSWERS ->  FirstWordBeginningProducer.produce(answersDictionary);
        };

        return new OneActionFilteringService(
            answersDictionary.getWords(), beginnings, Level.INFO, FILTERING_STRATEGY
        )
            .filterActions()
            .action();
    }

    private static Action createOptimalFewWordsBeginning(Dictionary allWordsDictionary, Dictionary answersDictionary,
                                                         int wordsCount) {
        log.info("Searching optimal {} words beginning", wordsCount);

        List<Action> beginnings = LetterFrequencyUniqueBeginningProducer
            .produce(allWordsDictionary, answersDictionary, wordsCount);

        return new OneActionFilteringService(
            answersDictionary.getWords(), beginnings, Level.INFO, FILTERING_STRATEGY
        )
            .filterActions()
            .action();
    }



    private static String formatStepsStatsString(SortedMap<Integer, Integer> stepsCountStats) {
        String tabulation = "    ";
        StringBuilder sb = new StringBuilder();

        for (Entry<Integer, Integer> entry : stepsCountStats.entrySet()) {
            sb.append('\n')
                .append(tabulation)
                .append(entry.getKey())
                .append(": ")
                .append(entry.getValue());
        }

        return sb.toString();

    }
}
