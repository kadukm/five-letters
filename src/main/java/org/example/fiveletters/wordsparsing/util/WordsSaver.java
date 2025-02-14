package org.example.fiveletters.wordsparsing.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WordsSaver {

    private final List<String> allWords = new ArrayList<>();

    public void add(Collection<String> words) {
        allWords.addAll(words);
    }

    public void save(String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            allWords.forEach(writer::println);
        }
    }
}
