package org.example.fiveletters.solving.uniquebeginningsearch.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.util.MaskUtils;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.Beginning;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.UniqueLetterWord;

@Slf4j
public class BruteForceBeginningSearcher {

    protected final int wordCountToFind;
    protected final List<UniqueLetterWord> uniqueLetterWords;

    protected BruteForceBeginningSearcher(int wordCountToFind, List<UniqueLetterWord> uniqueLetterWords) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
    }

    public static List<Beginning> findBeginnings(List<Word> rawWords, int wordsCount) {
        BruteForceBeginningSearcher searcher = new BruteForceBeginningSearcher(wordsCount, filterWords(rawWords));

        List<Beginning> result = new ArrayList<>();
        Set<Integer> usedMasks = new HashSet<>();

        searcher.findBeginningsInternal(result, usedMasks, 0, new Beginning());

        return result;
    }

    private static List<UniqueLetterWord> filterWords(List<Word> rawWords) {
        return rawWords.stream()
            .map(UniqueLetterWord::tryParse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }

    private void findBeginningsInternal(
        List<Beginning> result,
        Set<Integer> usedMasks,
        int currentMask,
        Beginning currentBeginning
    ) {
        for (UniqueLetterWord uniqueLetterWord : uniqueLetterWords) {
            int nextMask = currentMask | uniqueLetterWord.getMask();
            if (usedMasks.contains(nextMask)) {
                continue;
            }

            Beginning nextBeginning = currentBeginning.addWord(uniqueLetterWord.getWord());

            if (nextBeginning.getWordsCount() == wordCountToFind) {
                result.add(nextBeginning);

                String message = "%s - %s".formatted(
                    String.join(" ", nextBeginning.getWords().stream().map(Word::toString).toList()),
                    MaskUtils.maskToString(nextMask)
                );
                log.info(message);
            } else {
                BruteForceBeginningSearcher nextSearcher = remove(uniqueLetterWord);
                nextSearcher.findBeginningsInternal(result, usedMasks, nextMask, nextBeginning);
            }

            usedMasks.add(nextMask);
        }
    }

    private BruteForceBeginningSearcher remove(UniqueLetterWord wordToRemove) {
        return new BruteForceBeginningSearcher(wordCountToFind, filterUniqueLetterWords(wordToRemove));
    }

    private List<UniqueLetterWord> filterUniqueLetterWords(UniqueLetterWord wordToRemove) {
        return uniqueLetterWords.stream()
            .filter(word -> (word.getMask() & wordToRemove.getMask()) == 0)
            .toList();
    }
}
