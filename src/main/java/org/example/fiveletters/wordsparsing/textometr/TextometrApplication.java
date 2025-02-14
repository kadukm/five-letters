package org.example.fiveletters.wordsparsing.textometr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.wordsparsing.textometr.service.FrequencyWordSaver;
import org.example.fiveletters.wordsparsing.textometr.service.TextometrService;

@Slf4j
public class TextometrApplication {

    public static void main(String[] args) throws IOException {
        List<String> words = Files.readAllLines(Path.of("dictionaries/open-corpora.txt"));
//        List<String> words = Files.readAllLines(Path.of("dictionaries/поиск-слов.txt"));

        FrequencyWordSaver saver = new FrequencyWordSaver();
        TextometrService textometrService = new TextometrService();

        for (int i = 0; i < words.size(); i++) {
            textometrService.getFrequency(words.get(i))
                .ifPresent(saver::add);

            logProgress(i, words.size());
        }

        saver.save("dictionaries/frequency/open-corpora-frequency.txt");
//        saver.save("dictionaries/frequency/поиск-слов-frequency.txt");
    }

    private static void logProgress(int i, int tuplesCount) {
        if (i % 100 != 0 && i + 1 != tuplesCount) {
            return;
        }

        String message = String.format("Processing progress: %.2f%% (%d/%d)",
                                       ((double) i+1)/tuplesCount*100, (i+1), tuplesCount);
        log.info(message);
    }
}
