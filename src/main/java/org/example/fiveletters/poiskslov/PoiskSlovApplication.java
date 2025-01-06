package org.example.fiveletters.poiskslov;

import org.example.fiveletters.poiskslov.service.Crawler;
import org.example.fiveletters.poiskslov.service.Parser;
import org.example.fiveletters.util.WordsSaver;

public class PoiskSlovApplication {

    private static final String LETTERS = "абвгдеёжзийклмнопрстуфхцчшщэюя";

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        WordsSaver wordsSaver = new WordsSaver();

        for (char letter : LETTERS.toCharArray()) {
            int page = 0;
            Parser parser;

            do {
                String html = crawler.getHtml(letter, page);
                parser = new Parser(html);
                wordsSaver.add(parser.parseWords());

                page++;
            } while (parser.hasNextPage());
        }

        wordsSaver.save("dictionaries/поиск-слов.txt");
    }
}
