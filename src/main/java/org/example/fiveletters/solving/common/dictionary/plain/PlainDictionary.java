package org.example.fiveletters.solving.common.dictionary.plain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlainDictionary implements Dictionary {

    private final List<Word> words;

    public static PlainDictionary read(String filename) throws IOException {
        List<Word> words;

        try (Stream<String> lines = Files.lines(Path.of(filename))) {
            words = lines
                .map(Word::new)
                .toList();
        }

        return new PlainDictionary(words);
    }
}
