package org.example.fiveletters.solving.common.dictionary.all;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AllWordsDictionary implements Dictionary {

    private final Set<Word> words;

    public static AllWordsDictionary read(String filename, WordSource... additionalSources) throws IOException {
        Set<Word> words;

        Set<WordSource> additionalSourceSet = Set.of(additionalSources);

        try (Stream<String> lines = Files.lines(Path.of(filename))) {
            words = lines
                .map(AllWordsDictionary::parseFromCsvLine)
                .filter(dto -> !Boolean.FALSE.equals(dto.exists()))
                .filter(dto -> Boolean.TRUE.equals(dto.exists()) || additionalSourceSet.contains(dto.source()))
                .map(WordDto::value)
                .map(Word::new)
                .collect(Collectors.toSet());
        }

        return new AllWordsDictionary(words);
    }

    private static WordDto parseFromCsvLine(String line) {
        String[] csvValues = line.split(";");

        WordSource source = WordSource.parse(csvValues[1]);

        Boolean exists = null;
        if (csvValues.length > 2) {
            exists = switch (csvValues[2]) {
                case "exists" -> Boolean.TRUE;
                case "not-exists" -> Boolean.FALSE;
                default -> throw new IllegalStateException("Unexpected word existing value: " + exists);
            };
        }

        return new WordDto(csvValues[0], source, exists);
    }
}
