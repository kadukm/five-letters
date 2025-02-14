package org.example.fiveletters.solving.common.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;

@Slf4j
public class DictionariesChecker {

    private DictionariesChecker() {
        throw new UnsupportedOperationException();
    }

    public static void check(Dictionary allWordsDictionary, Dictionary answersDictionary) {
        Set<Word> allWords = new HashSet<>(allWordsDictionary.getWords());

        List<Word> notFoundWords = answersDictionary.getWords().stream()
            .filter(answer -> !allWords.contains(answer))
            .toList();

        if (!notFoundWords.isEmpty()) {
            throw new IllegalStateException("All words dictionary doesn't contain answers: " + notFoundWords);
        }
    }
}
