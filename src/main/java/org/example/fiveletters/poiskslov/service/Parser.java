package org.example.fiveletters.poiskslov.service;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.util.WordMatcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

@Slf4j
public class Parser {

    private final Document doc;

    public Parser(String html) {
        doc = Jsoup.parse(html);
    }

    public boolean hasNextPage() {
        Elements nextPage = doc.select("li.pager__item.is-active + li.pager__item:not(.pager__item--next)");
        return !nextPage.isEmpty();
    }

    public List<String> parseWords() {
        List<String> parsedWords = doc.select("div.word-length-5.page-suschestvitelnye a.nsob").eachText();

        List<String> result = new ArrayList<>(parsedWords.size());

        for (String word : parsedWords) {
            if (!WordMatcher.MATCH_PREDICATE.test(word)) {
                log.warn("Word {} in incorrect format, skip it", word);
                continue;
            }

            result.add(word.toLowerCase());
        }

        return result;
    }
}
