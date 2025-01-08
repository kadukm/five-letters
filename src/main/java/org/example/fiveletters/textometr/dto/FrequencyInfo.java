package org.example.fiveletters.textometr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FrequencyInfo {

    private String lemma;

    @JsonProperty("rnc_all_ipm")
    private double frequency;

    @JsonProperty("not_in_list")
    private boolean notInList;
}
