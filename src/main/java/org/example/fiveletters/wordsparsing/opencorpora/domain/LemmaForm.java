package org.example.fiveletters.wordsparsing.opencorpora.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import lombok.Data;

@Data
public class LemmaForm {

    @JsonProperty("t")
    private String word;

    @JsonProperty("g")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Grammeme> grammemes;
}
