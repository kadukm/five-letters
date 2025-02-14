package org.example.fiveletters.wordsparsing.opencorpora.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import java.util.List;
import lombok.Data;

@Data
public class Lemma {

    @JsonProperty("l")
    private LemmaForm zeroForm;

    @JsonProperty("f")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<LemmaForm> forms;
}
