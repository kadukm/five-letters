package org.example.fiveletters.poiskslov;

import org.example.fiveletters.poiskslov.service.Crawler;
import org.example.fiveletters.poiskslov.service.Parser;
import org.example.fiveletters.poiskslov.service.Saver;

public class PoiskSlovApplication {

    private static final String LETTERS = "абвгдеёжзийклмнопрстуфхцчшщэюя";

    public static void main(String[] args) throws Exception {
        Crawler crawler = new Crawler();
        Saver saver = new Saver();

        for (char letter : LETTERS.toCharArray()) {
            int page = 0;
            Parser parser;

            do {
                String html = crawler.getHtml(letter, page);
                parser = new Parser(html);
                saver.add(parser.parseWords());

                page++;
            } while (parser.hasNextPage());
        }

        saver.save();
    }
}
