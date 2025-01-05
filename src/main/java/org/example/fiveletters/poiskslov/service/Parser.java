package org.example.fiveletters.poiskslov.service;

import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
        return doc.select("div.word-length-5.page-suschestvitelnye a.nsob").eachText();
    }
}
