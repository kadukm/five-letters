package org.example.fiveletters.solving.beginningsearch.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
    private final Comparator<Word> wordComparator;

    protected LetterFrequencyUniqueBeginningProducer(int wordCountToFind,
                                                     List<UniqueLetterWord> uniqueLetterWords,
                                                     Comparator<Word> wordComparator) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
        this.wordComparator = wordComparator;
    }

    public static List<Action> produce(Dictionary allWordsDictionary, Dictionary answersDictionary, int wordsCount) {
        List<Integer> searchMasks = new SearchMaskFinder().findBestSearchMasks(answersDictionary, wordsCount);

        Comparator<Word> wordComparator = Comparator.comparing(w -> answersDictionary.getWords().contains(w) ? -1 : 0);

        return searchMasks.stream()
            .map(searchMask -> LetterFrequencyUniqueBeginningProducer
                .produce(allWordsDictionary, wordComparator, wordsCount, searchMask)
            )
            .flatMap(Collection::stream)
            .toList();
    }

    private static List<Action> produce(Dictionary dictionary, Comparator<Word> wordComparator,
                                        int wordsCount, int searchMask) {
        LetterFrequencyUniqueBeginningProducer searcher = new LetterFrequencyUniqueBeginningProducer(
            wordsCount, filterWords(dictionary.getWords(), searchMask), wordComparator
        );

        List<Action> result = new ArrayList<>();
        Set<Set<Word>> usedBeginnings = new HashSet<>();

        searcher.produceInternal(result, usedBeginnings, Set.of());

        return result;
    }

    private static List<UniqueLetterWord> filterWords(Set<Word> rawWords, int searchMask) {
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

    private void produceInternal(List<Action> result, Set<Set<Word>> usedBeginnings, Set<Word> currentBeginning) {
        for (UniqueLetterWord uniqueLetterWord : uniqueLetterWords) {
            Set<Word> nextBeginning = new HashSet<>(currentBeginning);
            nextBeginning.add(uniqueLetterWord.getWord());

            if (usedBeginnings.contains(nextBeginning)) {
                continue;
            }

            if (nextBeginning.size() == wordCountToFind) {
                result.add(buildAction(nextBeginning));

                log.debug(String.join(" ", nextBeginning.stream().map(Word::toString).toList()));
            } else {
                LetterFrequencyUniqueBeginningProducer nextSearcher = remove(uniqueLetterWord);
                nextSearcher.produceInternal(result, usedBeginnings, nextBeginning);
            }

            usedBeginnings.add(nextBeginning);
        }
    }

    private Action buildAction(Set<Word> beginning) {
        List<Word> beginningList = beginning.stream()
            .sorted(wordComparator)
            .toList();

        return new Action(beginningList);
    }

    private LetterFrequencyUniqueBeginningProducer remove(UniqueLetterWord wordToRemove) {
        return new LetterFrequencyUniqueBeginningProducer(
            wordCountToFind, filterUniqueLetterWords(wordToRemove), wordComparator
        );
    }

    private List<UniqueLetterWord> filterUniqueLetterWords(UniqueLetterWord wordToRemove) {
        return uniqueLetterWords.stream()
            .filter(word -> (word.getMask() & wordToRemove.getMask()) == 0)
            .toList();
    }
}
