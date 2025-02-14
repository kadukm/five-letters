package org.example.fiveletters.solving.beginningsearch.dto;

import java.util.Optional;
import lombok.Getter;
import org.example.fiveletters.solving.common.domain.Letter;
import org.example.fiveletters.solving.common.domain.Word;

@Getter
public class UniqueLetterWord {

    private final Word word;
    private final int mask;

    private UniqueLetterWord(Word word) {
        this.word = word;
        this.mask = calculateMask(word);
    }

    public static Optional<UniqueLetterWord> tryParse(Word word) {
        if (word.getUniqueLetters().size() != word.getLength()) {
            return Optional.empty();
        }

        return Optional.of(new UniqueLetterWord(word));
    }

    private int calculateMask(Word word) {
        int result = 0;

        for (Letter letter : word.getLetters()) {
            result |= letter.mask;
        }

        return result;
    }

    @Override
    public String toString() {
        return word.toString();
    }
}
