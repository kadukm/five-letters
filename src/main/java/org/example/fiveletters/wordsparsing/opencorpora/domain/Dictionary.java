package org.example.fiveletters.wordsparsing.opencorpora.domain;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dictionary {

    private List<Lemma> lemmata;
}
