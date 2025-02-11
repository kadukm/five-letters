package org.example.fiveletters.solving.beginningsearch.firstword;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.engine.dto.Action;
import org.example.fiveletters.solving.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.engine.service.ActionFilteringService;

@Slf4j
public class FirstWordBeginningSearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read(
            "dictionaries/all-words.csv",
            Set.of(WordSource.OPEN_CORPORA, WordSource.HAND_INPUT)
        );
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        List<Action> beginnings = allWordsDictionary.getWords().stream()
            .map(w -> new Action(Set.of(w)))
            .toList();

        FilteringResult filteringResult = new ActionFilteringService(answersDictionary.getWords(), beginnings).filterActions();

        log.info(
            """
            Found best beginning:
            word: {}
            average remaining answers count: {}
            max remaining answers count: {}
            """,
            filteringResult.action().getWords().stream().findFirst().orElseThrow(),
            filteringResult.averageRemainingAnswersCount(),
            filteringResult.maxRemainingAnswersCount()
        );
    }
}
