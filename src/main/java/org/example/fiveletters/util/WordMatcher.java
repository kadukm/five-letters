package org.example.fiveletters.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class WordMatcher {

    public static final Predicate<String> MATCH_PREDICATE =
        Pattern.compile("[а-яёА-ЯЁ]{5}").asMatchPredicate();

    private WordMatcher() {
        throw new UnsupportedOperationException();
    }
}
