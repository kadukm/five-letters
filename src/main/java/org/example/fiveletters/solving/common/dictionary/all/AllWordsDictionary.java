package org.example.fiveletters.solving.common.dictionary.all;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AllWordsDictionary implements Dictionary {

    private final List<Word> words;

    public static AllWordsDictionary read(String filename, Set<WordSource> sources) throws IOException {
        List<Word> words;

        try (Stream<String> lines = Files.lines(Path.of(filename))) {
            words = lines
                .map(WordDto::parseFromCsvLine)
                .filter(dto -> Boolean.FALSE != dto.exists())
                .filter(dto -> sources.contains(dto.source()))
                .map(WordDto::value)
                .map(Word::new)
                .toList();
        }

        return new AllWordsDictionary(words);
    }
}
