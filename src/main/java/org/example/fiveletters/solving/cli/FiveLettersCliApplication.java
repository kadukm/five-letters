package org.example.fiveletters.solving.cli;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.example.fiveletters.solving.cli.service.CliService;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.dictionary.all.AllWordsDictionary;
import org.example.fiveletters.solving.common.dictionary.all.WordSource;
import org.example.fiveletters.solving.common.dictionary.plain.PlainDictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.util.DictionariesChecker;

public class FiveLettersCliApplication {

    public static void main(String[] args) throws IOException {
        Dictionary allWordsDictionary = AllWordsDictionary.read("dictionaries/all-words.csv", WordSource.OPEN_CORPORA);
        Dictionary answersDictionary = PlainDictionary.read("dictionaries/plain/tbank-answers-assumption.txt");

        DictionariesChecker.check(allWordsDictionary, answersDictionary);

        Action beginning = createCustomBeginning();

        new CliService(allWordsDictionary, answersDictionary).run(beginning);
    }

    private static Action createCustomBeginning(String ... values) {
        List<Word> words = Stream.of(values)
            .map(Word::new)
            .toList();

        return new Action(words);
    }
}
