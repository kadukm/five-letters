package org.example.fiveletters.textometr.dto;

import java.util.List;
import lombok.Data;

@Data
public class FrequencyResponse {

    private List<FrequencyInfo> table;
}
