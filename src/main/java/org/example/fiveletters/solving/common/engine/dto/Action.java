package org.example.fiveletters.solving.common.engine.dto;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import org.example.fiveletters.solving.common.domain.Word;

@Data
public class Action {

    private final List<Word> words;

    public Action() {
        words = List.of();
    }

    public Action(Collection<Word> words) {
        this.words = words.stream().toList();
    }

    public Action addWord(Word word) {
        List<Word> newWords = Stream
            .concat(words.stream(), Stream.of(word))
            .toList();

        return new Action(newWords);
    }

    public int getWordsCount() {
        return words.size();
    }
}
