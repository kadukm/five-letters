package org.example.fiveletters.solving.common.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.fiveletters.solving.common.domain.Letter;

public class LetterUtils {

    public static final Map<Character, Letter> LETTERS;
    public static final List<Letter> VOWELS;

    static {
        LETTERS = instantiateAllLetters();
        VOWELS = instantiateVowels();
    }

    private static Map<Character, Letter> instantiateAllLetters() {
        return Stream.of(Letter.values())
            .collect(Collectors.toMap(l -> l.value, Function.identity()));
    }

    private static List<Letter> instantiateVowels() {
        return Stream.of('а', 'у', 'о', 'и', 'э', 'ы', 'я', 'ю', 'е')
            .map(LetterUtils::getLetterByValue)
            .toList();
    }

    private LetterUtils() {
        throw new UnsupportedOperationException();
    }

    public static Letter getLetterByValue(char c) {
        Letter result = LETTERS.get(c);
        if (result == null) {
            throw new IllegalArgumentException("Unexpected letter " + c);
        }

        return result;
    }
}
