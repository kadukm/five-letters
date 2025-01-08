package org.example.fiveletters.textometr.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.example.fiveletters.textometr.domain.FrequencyWord;
import org.example.fiveletters.textometr.dto.FrequencyRequest;
import org.example.fiveletters.textometr.dto.FrequencyResponse;

@Slf4j
public class TextometrService {

    private static final String BASE_URL = "https://api.textometr.ru/frequency";

    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    public TextometrService() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        httpClient = HttpClientBuilder.create().build();
    }

    public Optional<FrequencyWord> getFrequency(String word) throws IOException {
        FrequencyRequest request = new FrequencyRequest(word);
        FrequencyResponse response = requestFrequencies(request);
        return parseResponse(response, word);
    }

    private FrequencyResponse requestFrequencies(FrequencyRequest request) throws IOException {
        HttpPost httpRequest = new HttpPost(BASE_URL);
        httpRequest.setHeader("Content-Type", ContentType.APPLICATION_JSON);
        httpRequest.setEntity(new StringEntity(objectMapper.writeValueAsString(request)));

        return httpClient.execute(
            httpRequest,
            response -> objectMapper.readValue(EntityUtils.toString(response.getEntity()), FrequencyResponse.class)
        );
    }

    private Optional<FrequencyWord> parseResponse(FrequencyResponse response, String word) {
        return response.getTable().stream()
            .findFirst()
            .filter(frequencyInfo -> !frequencyInfo.isNotInList())
            .filter(frequencyInfo -> {
                if (!wordEqualsToLemma(word, frequencyInfo.getLemma())) {
                    log.info("Word {} isn't equal to lemma {}", word, frequencyInfo.getLemma());
                    return false;
                }
                return true;
            })
            .map(frequencyInfo -> new FrequencyWord(word, frequencyInfo.getFrequency()));
    }

    private boolean wordEqualsToLemma(String word, String lemma) {
        if (word.length() != lemma.length()) {
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char wordChar = getChar(word, i);
            char lemmaChar = getChar(lemma, i);

            if (wordChar != lemmaChar) {
                return false;
            }
        }

        return true;
    }

    private char getChar(String s, int i) {
        char c = s.charAt(i);
        return c == 'ё' ? 'е' : c;
    }
}
