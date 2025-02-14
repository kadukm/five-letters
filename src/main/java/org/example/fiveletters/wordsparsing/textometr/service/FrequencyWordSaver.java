package org.example.fiveletters.wordsparsing.textometr.service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.TreeSet;
import org.example.fiveletters.wordsparsing.textometr.domain.FrequencyWord;

public class FrequencyWordSaver {

    private final TreeSet<FrequencyWord> allFrequencyWords = new TreeSet<>();

    public void add(FrequencyWord frequencyWord) {
        allFrequencyWords.add(frequencyWord);
    }

    public void save(String fileName) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(file, StandardCharsets.UTF_8)) {
            for (FrequencyWord frequencyWord : allFrequencyWords) {
                writer.println(frequencyWord.word() + " " + frequencyWord.frequency());
            }
        }
    }
}
