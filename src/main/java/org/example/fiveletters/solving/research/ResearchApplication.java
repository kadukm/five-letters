package org.example.fiveletters.solving.research;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.util.DictionariesChecker;
import org.example.fiveletters.solving.research.dto.SummaryStats;
import org.example.fiveletters.solving.research.service.StatsCalculator;

@Slf4j
public class ResearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read("dictionaries/all-words.csv", WordSource.OPEN_CORPORA);
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        DictionariesChecker.check(allWordsDictionary, answersDictionary);

        Action beginning = createCustomBeginning("норка");
//        Action beginning = createCustomBeginning("серна", "колит");
//        Action beginning = createCustomBeginning("сплин", "метод", "курва");
//        Action beginning = createCustomBeginning("гниль", "сброд", "пушка", "взмет");
//        Action beginning = createCustomBeginning("кольт", "бридж", "взмах", "пешня", "сычуг");
//        Action beginning = createCustomBeginning("шприц", "чувяк", "лохмы", "съезд", "фьюжн", "гбайт");

        SummaryStats summary = StatsCalculator.calculate(allWordsDictionary, answersDictionary, beginning);

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
