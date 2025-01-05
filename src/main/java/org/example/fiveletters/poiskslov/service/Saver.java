package org.example.fiveletters.poiskslov.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Saver {

    private static final Predicate<String> WORD_MATCH_PREDICATE =
        Pattern.compile("[а-яёА-ЯЁ]{5}").asMatchPredicate();

    private final List<String> allWords = new ArrayList<>();

    public void add(List<String> words) {
        for (String word : words) {
            if (!WORD_MATCH_PREDICATE.test(word)) {
                log.warn("Word {} in incorrect format, skip it", word);
                continue;
            }

            allWords.add(word.toLowerCase());
        }
    }

    public void save() throws IOException {
        try (PrintWriter writer = new PrintWriter("поиск-слов.txt", StandardCharsets.UTF_8)) {
            allWords.forEach(writer::println);
        }
    }
}
