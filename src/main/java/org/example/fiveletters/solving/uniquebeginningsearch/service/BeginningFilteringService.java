package org.example.fiveletters.solving.uniquebeginningsearch.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.engine.dto.State;
import org.example.fiveletters.solving.engine.service.FiveLettersEngine;
import org.example.fiveletters.solving.uniquebeginningsearch.dto.Beginning;

@Slf4j
public class BeginningFilteringService {

    private final List<Beginning> beginnings;

    public BeginningFilteringService(List<Beginning> beginnings) {
        this.beginnings = beginnings;
    }

    public Beginning filterBeginnings(Dictionary answersDictionary) {
        FiveLettersEngine engine = new FiveLettersEngine();

        Beginning bestBeginning = beginnings.getFirst();
        int bestRemainingAnswers = Integer.MAX_VALUE;

        State state = State.createInitialState(answersDictionary.getWords());
        for (int i = 0; i < beginnings.size(); i++) {
            Beginning beginning = beginnings.get(i);

            int remainingAnswersSum = 0;

            for (Word answer : answersDictionary.getWords()) {
                State nextState = state;

                for (Word word : beginning.getWords()) {
                    nextState = engine.doNextStep(nextState, word, answer);
                }

                remainingAnswersSum += nextState.getPossibleAnswers().size();
            }

            if (remainingAnswersSum < bestRemainingAnswers) {
                bestRemainingAnswers = remainingAnswersSum;
                bestBeginning = beginning;
            }

            logProgress(i, beginnings.size());
        }

        return bestBeginning;
    }

    private void logProgress(int i, int allCount) {
        if (i % 100 != 0 && i + 1 != allCount) {
            return;
        }

        String message = String.format("Processing progress: %.2f%% (%d/%d)",
                                       ((double) i+1)/allCount*100, (i+1), allCount);
        log.info(message);
    }
}
