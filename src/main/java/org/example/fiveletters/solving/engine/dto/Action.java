package org.example.fiveletters.solving.engine.dto;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.fiveletters.solving.common.domain.Word;

@Data
@RequiredArgsConstructor
public class Action {

    private final Set<Word> words;

    public Action() {
        words = Set.of();
    }

    public Action addWord(Word word) {
        HashSet<Word> newWords = new HashSet<>(words);
        newWords.add(word);

        return new Action(newWords);
    }

    public int getWordsCount() {
        return words.size();
    }
}
