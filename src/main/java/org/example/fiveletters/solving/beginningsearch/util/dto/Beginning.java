package org.example.fiveletters.solving.beginningsearch.util.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fiveletters.solving.common.domain.Word;

@Data
@RequiredArgsConstructor
public class Beginning {

    private final Set<Word> words;

    public Beginning() {
        words = Set.of();
    }

    public Beginning addWord(Word word) {
        HashSet<Word> newWords = new HashSet<>(words);
        newWords.add(word);

        return new Beginning(newWords);
    }

    public int getWordsCount() {
        return words.size();
    }
}
