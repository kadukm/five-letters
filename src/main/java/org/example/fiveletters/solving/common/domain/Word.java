package org.example.fiveletters.solving.common.domain;

import java.util.Set;
import lombok.Getter;
import org.example.fiveletters.solving.common.util.LetterCounter;
import org.example.fiveletters.solving.common.util.LetterUtils;

@Getter
public class Word {

    private final String value;

    private final Letter[] letters;

    private final LetterCounter letterCounter;

    public Word(String value) {
        value = value
            .toLowerCase()
            .replace('ё', 'е');

        this.value = value;
        this.letters = parseLetters(value);
        this.letterCounter = parseLetterCount(letters);
    }

    private Letter[] parseLetters(String value) {
        Letter[] result = new Letter[5];
        for (int i = 0; i < 5; i++) {
            result[i] = LetterUtils.getLetterByValue(value.charAt(i));
        }
        return result;
    }

    private LetterCounter parseLetterCount(Letter[] letters) {
        LetterCounter result = new LetterCounter();

        for (Letter l : letters) {
            result.increase(l);
        }

        return result;
    }

    public int getLength() {
        return letters.length;
    }

    public Letter getLetterAt(int i) {
        return letters[i];
    }

    public char getCharAt(int i) {
        return letters[i].value;
    }

    public int getMaskAt(int i) {
        return letters[i].mask;
    }

    public Set<Letter> getUniqueLetters() {
        return letterCounter.keySet();
    }

    public LetterCounter copyLetterCounter() {
        return letterCounter.copy();
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Word w) {
            return w.value.equals(value);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
