package org.example.fiveletters.solving.uniquebeginningsearch.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.Beginning;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.UniqueLetterWord;

@Slf4j
public class LetterFrequencyBeginningSearcher{

    protected final int wordCountToFind;
    protected final List<UniqueLetterWord> uniqueLetterWords;

    protected LetterFrequencyBeginningSearcher(int wordCountToFind, List<UniqueLetterWord> uniqueLetterWords) {
        this.wordCountToFind = wordCountToFind;
        this.uniqueLetterWords = uniqueLetterWords;
    }

    public static List<Beginning> findBeginnings(List<Word> rawWords, int wordsCount, int searchMask) {
        LetterFrequencyBeginningSearcher searcher = new LetterFrequencyBeginningSearcher(
            wordsCount, filterWords(rawWords, searchMask));

        List<Beginning> result = new ArrayList<>();
        Set<Beginning> usedBeginnings = new HashSet<>();

        searcher.findBeginningsInternal(result, usedBeginnings, new Beginning());

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
        List<Beginning> result,
        Set<Beginning> usedBeginnings,
        Beginning currentBeginning
    ) {
        for (UniqueLetterWord uniqueLetterWord : uniqueLetterWords) {
            Beginning nextBeginning = currentBeginning.addWord(uniqueLetterWord.getWord());
            if (usedBeginnings.contains(nextBeginning)) {
                continue;
            }

            if (nextBeginning.getWordsCount() == wordCountToFind) {
                result.add(nextBeginning);

                log.info(String.join(" ", nextBeginning.getWords().stream().map(Word::toString).toList()));
            } else {
                LetterFrequencyBeginningSearcher nextSearcher = remove(uniqueLetterWord);
                nextSearcher.findBeginningsInternal(result, usedBeginnings, nextBeginning);
            }

            usedBeginnings.add(nextBeginning);
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
