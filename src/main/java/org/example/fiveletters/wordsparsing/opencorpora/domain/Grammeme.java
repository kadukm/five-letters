package org.example.fiveletters.wordsparsing.opencorpora.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Grammeme {

    @JsonProperty("v")
    private String value;
}
