package org.example.fiveletters.solving.cli;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.example.fiveletters.solving.cli.service.CliService;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.engine.dto.Action;

public class FiveLettersCliApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read(
            "dictionaries/all-words.csv",
            Set.of(WordSource.OPEN_CORPORA, WordSource.HAND_INPUT)
        );
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

//        Action beginning = createCustomBeginning();
//        Action beginning = createCustomBeginning("норка");
        Action beginning = createCustomBeginning("сплин", "курва", "метод");
//        Action beginning = createCustomBeginning("гниль", "пушка", "сброд", "взмет");

        new CliService(allWordsDictionary, answersDictionary).run(beginning);
    }

    private static Action createCustomBeginning(String ... values) {
        Set<Word> words = Stream.of(values)
            .map(Word::new)
            .collect(Collectors.toSet());

        return new Action(words);
    }
}
