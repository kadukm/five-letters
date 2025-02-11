package org.example.fiveletters.solving.beginningsearch.uniqueletters.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.engine.dto.Action;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.beginningsearch.uniqueletters.dto.UniqueLetterWord;

@Slf4j
public class LetterFrequencyBeginningSearcher{

    protected final int wordCountToFind;
    protected final List<UniqueLetterWord> uniqueLetterWords;

    protected LetterFrequencyBeginningSearcher(int wordCountToFind, List<UniqueLetterWord> uniqueLetterWords) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
    }

    public static List<Action> findBeginnings(List<Word> rawWords, int wordsCount, int searchMask) {
        LetterFrequencyBeginningSearcher searcher = new LetterFrequencyBeginningSearcher(
            wordsCount, filterWords(rawWords, searchMask)
        );

        List<Action> result = new ArrayList<>();
        Set<Action> usedActions = new HashSet<>();

        searcher.findBeginningsInternal(result, usedActions, new Action());

        return result;
    }

    private static List<UniqueLetterWord> filterWords(List<Word> rawWords, int searchMask) {
        return rawWords.stream()
            .map(UniqueLetterWord::tryParse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .filter(w -> filterWordBySearchMask(w, searchMask))
            .toList();
    }

    private static boolean filterWordBySearchMask(UniqueLetterWord word, int searchMask) {
        int conjunctionWithSearchMask = searchMask & word.getMask();
        return conjunctionWithSearchMask == word.getMask();
    }

    private void findBeginningsInternal(
        List<Action> result,
        Set<Action> usedActions,
        Action currentAction
    ) {
        for (UniqueLetterWord uniqueLetterWord : uniqueLetterWords) {
            Action nextAction = currentAction.addWord(uniqueLetterWord.getWord());
            if (usedActions.contains(nextAction)) {
                continue;
            }

            if (nextAction.getWordsCount() == wordCountToFind) {
                result.add(nextAction);

                log.debug(String.join(" ", nextAction.getWords().stream().map(Word::toString).toList()));
            } else {
                LetterFrequencyBeginningSearcher nextSearcher = remove(uniqueLetterWord);
                nextSearcher.findBeginningsInternal(result, usedActions, nextAction);
            }

            usedActions.add(nextAction);
        }
    }

    private LetterFrequencyBeginningSearcher remove(UniqueLetterWord wordToRemove) {
        return new LetterFrequencyBeginningSearcher(wordCountToFind, filterUniqueLetterWords(wordToRemove));
    }

    private List<UniqueLetterWord> filterUniqueLetterWords(UniqueLetterWord wordToRemove) {
        return uniqueLetterWords.stream()
            .filter(word -> (word.getMask() & wordToRemove.getMask()) == 0)
            .toList();
    }
}
