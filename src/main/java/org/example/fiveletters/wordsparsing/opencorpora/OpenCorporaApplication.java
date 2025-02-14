package org.example.fiveletters.wordsparsing.opencorpora;

import java.io.IOException;
import java.util.Set;
import org.example.fiveletters.wordsparsing.opencorpora.service.Parser;
import org.example.fiveletters.wordsparsing.util.WordsSaver;

public class OpenCorporaApplication {

    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        Set<String> words = parser.parseWords();

        WordsSaver wordsSaver = new WordsSaver();
        wordsSaver.add(words);

        wordsSaver.save("dictionaries/plain/open-corpora.txt");
    }
}
