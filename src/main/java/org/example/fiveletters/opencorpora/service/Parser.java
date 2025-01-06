package org.example.fiveletters.opencorpora.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.opencorpora.domain.Dictionary;
import org.example.fiveletters.opencorpora.domain.Grammeme;
import org.example.fiveletters.opencorpora.domain.Lemma;
import org.example.fiveletters.opencorpora.domain.LemmaForm;
import org.example.fiveletters.opencorpora.util.GrammemeValues;
import org.example.fiveletters.util.WordMatcher;

@Slf4j
public class Parser {

    private static final Set<String> UNSUPPORTED_GRAMMEME_VALUES = Set.of(
        GrammemeValues.ABBR,
        GrammemeValues.NAME,
        GrammemeValues.SURN,
        GrammemeValues.PATR,
        GrammemeValues.ORGN,
        GrammemeValues.TRAD,
        GrammemeValues.GEOX
    );
    private static final Set<String> SING_NOMN_GRAMMEME_VALUES = Set.of(
        GrammemeValues.SING,
        GrammemeValues.NOMN
    );
    private static final Set<String> NOMN_GRAMMEME_VALUES = Set.of(
        GrammemeValues.NOMN
    );

    private final XmlMapper mapper;

    public Parser() {
        mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public Set<String> parseWords() throws IOException {
        File file = new File("dict.opcorpora.xml");
        Dictionary dict = mapper.readValue(file, Dictionary.class);

        return filter(dict);
    }

    private Set<String> filter(Dictionary dict) {
        return dict.getLemmata().stream()
            .filter(this::filterLemma)
            .map(this::tryGetFiveLetterWord)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    private boolean filterLemma(Lemma lemma) {
        LemmaForm zeroForm = lemma.getZeroForm();

        if (!lemmaFromContainsGrammeme(zeroForm, GrammemeValues.NOUN)) {
            return false;
        }

        for (Grammeme grammeme : zeroForm.getGrammemes()) {
            if (UNSUPPORTED_GRAMMEME_VALUES.contains(grammeme.getValue())) {
                return false;
            }
        }

        return true;
    }

    private Optional<String> tryGetFiveLetterWord(Lemma lemma) {
        boolean pltm = lemmaFromContainsGrammeme(lemma.getZeroForm(), GrammemeValues.PLTM);

        Optional<LemmaForm> lemmaForm = pltm
            ? tryGetLemmaFormByGrammemes(lemma, NOMN_GRAMMEME_VALUES)
            : tryGetLemmaFormByGrammemes(lemma, SING_NOMN_GRAMMEME_VALUES);

        Optional<String> optionalFiveLetterWord = lemmaForm
            .map(LemmaForm::getWord)
            .filter(word -> word.length() == 5);

        if (optionalFiveLetterWord.isEmpty()) {
            return Optional.empty();
        }

        String fiveLetterWord = optionalFiveLetterWord.get();
        if (!WordMatcher.MATCH_PREDICATE.test(fiveLetterWord)) {
            log.warn("Word {} in incorrect format, skip it", fiveLetterWord);
            return Optional.empty();
        }

        return Optional.of(fiveLetterWord.toLowerCase());
    }

    private boolean lemmaFromContainsGrammeme(LemmaForm lemmaForm, String grammemeValue) {
        for (Grammeme grammeme : lemmaForm.getGrammemes()) {
            if (grammeme.getValue().equals(grammemeValue)) {
                return true;
            }
        }

        return false;
    }

    private Optional<LemmaForm> tryGetLemmaFormByGrammemes(Lemma lemma, Set<String> grammemeValues) {
        for (LemmaForm lemmaForm : lemma.getForms()) {
            int remainingGrammemeCount = grammemeValues.size();

            for (Grammeme grammeme : lemmaForm.getGrammemes()) {
                if (grammemeValues.contains(grammeme.getValue())) {
                    remainingGrammemeCount--;
                }
            }

            if (remainingGrammemeCount == 0 && lemmaForm.getWord().length() == 5) {
                return Optional.of(lemmaForm);
            }
        }

        return Optional.empty();
    }
}
