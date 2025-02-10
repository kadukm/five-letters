package org.example.fiveletters.solving.uniquebeginningsearch;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.Beginning;
import org.example.fiveletters.solving.uniquebeginningsearch.service.BeginningFilteringService;
import org.example.fiveletters.solving.uniquebeginningsearch.service.BruteForceBeginningSearcher;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.uniquebeginningsearch.service.LetterFrequencyBeginningSearcher;
import org.example.fiveletters.solving.uniquebeginningsearch.service.SearchMaskFinder;

@Slf4j
public class UniqueBeginningSearchApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read(
            "dictionaries/all-words.csv",
            Set.of(WordSource.OPEN_CORPORA, WordSource.HAND_INPUT)
        );
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        int wordsCount = 4;

        // Даже на словаре частотных слов в 2000 слов брут-форс будет отрабатывать около 16 дней
//        List<Beginning> beginnings = findBestBeginningByBruteForce(allWordsDictionary, wordsCount);
        List<Beginning> beginnings = findBestBeginningsByLetterFrequency(allWordsDictionary, answersDictionary, wordsCount);

        log.info("Found beginnings: {}", beginnings.size());

        if (beginnings.isEmpty()) {
            log.warn("Cannot find beginning");
            return;
        }

        BeginningFilteringService beginningFilteringService = new BeginningFilteringService(beginnings);
        Beginning bestBeginning = beginningFilteringService.filterBeginnings(answersDictionary);

        log.info("Best beginning: {}", bestBeginning);
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
