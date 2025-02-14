package org.example.fiveletters.solving.beginningsearch;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.beginningsearch.service.FirstWordBeginningProducer;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.engine.dto.Action;
import org.example.fiveletters.solving.engine.dto.FilteringResult;
import org.example.fiveletters.solving.engine.service.ActionFilteringService;

@Slf4j
public class BeginningSearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read(
            "dictionaries/all-words.csv",
            Set.of(WordSource.OPEN_CORPORA, WordSource.HAND_INPUT)
        );
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        List<Action> beginnings = FirstWordBeginningProducer.produce(allWordsDictionary);
//        List<Action> beginnings = BruteForceUniqueBeginningProducer.produce(allWordsDictionary, 4);
//        List<Action> beginnings = LetterFrequencyUniqueBeginningProducer.produce(allWordsDictionary, answersDictionary, 4);

        log.info("Found beginnings: {}", beginnings.size());

        if (beginnings.isEmpty()) {
            log.warn("Cannot find beginning");
            return;
        }

        FilteringResult filteringResult = new ActionFilteringService(answersDictionary.getWords(), beginnings).filterActions();

        log.info(
            """
            Found best beginning:
            words: {}
            average remaining answers count: {}
            max remaining answers count: {}
            """,
            filteringResult.action().getWords(),
            filteringResult.averageRemainingAnswersCount(),
            filteringResult.maxRemainingAnswersCount()
        );
    }

}
