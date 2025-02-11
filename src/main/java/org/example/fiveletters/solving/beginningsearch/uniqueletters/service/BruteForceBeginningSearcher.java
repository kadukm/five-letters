package org.example.fiveletters.solving.beginningsearch.uniqueletters.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.engine.dto.Action;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.util.MaskUtils;
import org.example.fiveletters.solving.beginningsearch.uniqueletters.dto.UniqueLetterWord;

@Slf4j
public class BruteForceBeginningSearcher {

    protected final int wordCountToFind;
    protected final List<UniqueLetterWord> uniqueLetterWords;

    protected BruteForceBeginningSearcher(int wordCountToFind, List<UniqueLetterWord> uniqueLetterWords) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
    }

    public static List<Action> findBeginnings(List<Word> rawWords, int wordsCount) {
        BruteForceBeginningSearcher searcher = new BruteForceBeginningSearcher(wordsCount, filterWords(rawWords));

        List<Action> result = new ArrayList<>();
        Set<Integer> usedMasks = new HashSet<>();

        searcher.findBeginningsInternal(result, usedMasks, 0, new Action());

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
        List<Action> result,
        Set<Integer> usedMasks,
        int currentMask,
        Action currentAction
    ) {
        for (UniqueLetterWord uniqueLetterWord : uniqueLetterWords) {
            int nextMask = currentMask | uniqueLetterWord.getMask();
            if (usedMasks.contains(nextMask)) {
                continue;
            }

            Action nextAction = currentAction.addWord(uniqueLetterWord.getWord());

            if (nextAction.getWordsCount() == wordCountToFind) {
                result.add(nextAction);

                String message = "%s - %s".formatted(
                    String.join(" ", nextAction.getWords().stream().map(Word::toString).toList()),
                    MaskUtils.maskToString(nextMask)
                );
                log.info(message);
            } else {
                BruteForceBeginningSearcher nextSearcher = remove(uniqueLetterWord);
                nextSearcher.findBeginningsInternal(result, usedMasks, nextMask, nextAction);
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
