package org.example.fiveletters.solving.beginningsearch.uniqueletters;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.beginningsearch.util.dto.FilteringResult;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.beginningsearch.util.dto.Beginning;
import org.example.fiveletters.solving.beginningsearch.util.service.BeginningFilteringService;
import org.example.fiveletters.solving.beginningsearch.uniqueletters.service.BruteForceBeginningSearcher;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.beginningsearch.uniqueletters.service.LetterFrequencyBeginningSearcher;
import org.example.fiveletters.solving.beginningsearch.uniqueletters.service.SearchMaskFinder;

@Slf4j
public class UniqueLettersBeginningSearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read(
            "dictionaries/all-words.csv",
            Set.of(WordSource.OPEN_CORPORA, WordSource.HAND_INPUT)
        );
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        int wordsCount = 4;

//        List<Beginning> beginnings = findBestBeginningByBruteForce(allWordsDictionary, wordsCount);
        List<Beginning> beginnings = findBestBeginningsByLetterFrequency(allWordsDictionary, answersDictionary, wordsCount);

        log.info("Found beginnings: {}", beginnings.size());

        if (beginnings.isEmpty()) {
            log.warn("Cannot find beginning");
            return;
        }

        FilteringResult bestBeginning = new BeginningFilteringService(answersDictionary, beginnings).filterBeginnings();

        log.info(
            """
            Found best beginning:
            words: {}
            average remaining answers count: {}
            max remaining answers count: {}
            """,
            bestBeginning.beginning().getWords(),
            bestBeginning.averageRemainingAnswersCount(),
            bestBeginning.maxRemainingAnswersCount()
        );
    }

    private static List<Beginning> findBestBeginningByBruteForce(Dictionary allWordsDictionary, int wordsCount) {
        return BruteForceBeginningSearcher.findBeginnings(allWordsDictionary.getWords(), wordsCount);
    }

    private static List<Beginning> findBestBeginningsByLetterFrequency(Dictionary allWordsDictionary,
                                                                       Dictionary answersDictionary,
                                                                       int wordsCount) {
        List<Integer> searchMasks = new SearchMaskFinder().findBestSearchMasks(answersDictionary, wordsCount);

        return searchMasks.stream()
            .map(searchMask -> LetterFrequencyBeginningSearcher
                .findBeginnings(allWordsDictionary.getWords(), wordsCount, searchMask)
            )
            .flatMap(Collection::stream)
            .toList();
    }
}
