package org.example.fiveletters.solving.beginningsearch.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.beginningsearch.dto.UniqueLetterWord;

@Slf4j
public class LetterFrequencyUniqueBeginningProducer {

    private final int wordCountToFind;
    private final List<UniqueLetterWord> uniqueLetterWords;

    protected LetterFrequencyUniqueBeginningProducer(int wordCountToFind, List<UniqueLetterWord> uniqueLetterWords) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
    }

    public static List<Action> produce(Dictionary allWordsDictionary, Dictionary answersDictionary, int wordsCount) {
        List<Integer> searchMasks = new SearchMaskFinder().findBestSearchMasks(answersDictionary, wordsCount);

        return searchMasks.stream()
            .map(searchMask -> LetterFrequencyUniqueBeginningProducer
                .produce(allWordsDictionary, wordsCount, searchMask)
            )
            .flatMap(Collection::stream)
            .toList();
    }

    private static List<Action> produce(Dictionary dictionary, int wordsCount, int searchMask) {
        LetterFrequencyUniqueBeginningProducer searcher = new LetterFrequencyUniqueBeginningProducer(
            wordsCount, filterWords(dictionary.getWords(), searchMask)
        );

        List<Action> result = new ArrayList<>();
        Set<Action> usedActions = new HashSet<>();

        searcher.produceInternal(result, usedActions, new Action());

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

    private void produceInternal(
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
                LetterFrequencyUniqueBeginningProducer nextSearcher = remove(uniqueLetterWord);
                nextSearcher.produceInternal(result, usedActions, nextAction);
            }

            usedActions.add(nextAction);
        }
    }

    private LetterFrequencyUniqueBeginningProducer remove(UniqueLetterWord wordToRemove) {
        return new LetterFrequencyUniqueBeginningProducer(wordCountToFind, filterUniqueLetterWords(wordToRemove));
    }

    private List<UniqueLetterWord> filterUniqueLetterWords(UniqueLetterWord wordToRemove) {
        return uniqueLetterWords.stream()
            .filter(word -> (word.getMask() & wordToRemove.getMask()) == 0)
            .toList();
    }
}
