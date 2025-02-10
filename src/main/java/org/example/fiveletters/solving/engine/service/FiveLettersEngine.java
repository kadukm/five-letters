package org.example.fiveletters.solving.engine.service;

import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.util.LetterCounter;
import org.example.fiveletters.solving.engine.dto.GuessResponse;
import org.example.fiveletters.solving.engine.dto.GuessStats;
import org.example.fiveletters.solving.engine.dto.LetterStatus;
import org.example.fiveletters.solving.engine.dto.State;

public class FiveLettersEngine {

    public State doNextStep(State state, Word word, Word answer) {
        GuessResponse response = guess(answer, word);
        GuessStats guessStats = parseResponse(response);
        return state.applyGuess(guessStats);
    }

    private GuessResponse guess(Word answer, Word word) {
        LetterStatus[] letterStatuses = new LetterStatus[5];

        LetterCounter answerLetterCounter = answer.copyLetterCounter();

        for (int i = 0; i < 5; i++) {
            if (word.getCharAt(i) == answer.getCharAt(i)) {
                answerLetterCounter.tryDecrease(answer.getLetterAt(i));
                letterStatuses[i] = LetterStatus.EXACT_POSITION;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (letterStatuses[i] == LetterStatus.EXACT_POSITION) {
                continue;
            }

            if (answerLetterCounter.tryDecrease(word.getLetterAt(i))) {
                letterStatuses[i] = LetterStatus.OTHER_POSITION;
            } else {
                letterStatuses[i] = LetterStatus.NOT_PRESENT;
            }
        }

        return new GuessResponse(word.getLetters(), letterStatuses);
    }

    private GuessStats parseResponse(GuessResponse response) {
        GuessStats result = new GuessStats();

        for (int i = 0; i < 5; i++) {
            result.addLetter(response.getLetterAt(i), i, response.getStatusAt(i));
        }

        return result;
    }
}
