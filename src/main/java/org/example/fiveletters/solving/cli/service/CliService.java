package org.example.fiveletters.solving.cli.service;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.GuessResponse;
import org.example.fiveletters.solving.common.engine.dto.LetterStatus;
import org.example.fiveletters.solving.common.engine.dto.State;
import org.example.fiveletters.solving.common.engine.service.FiveLettersEngine;
import org.example.fiveletters.solving.common.engine.service.OptimalNextWordSearcher;

@Slf4j
public class CliService {

    private final Dictionary allWordsDictionary;
    private final Dictionary answersDictionary;

    private final FiveLettersEngine engine = new FiveLettersEngine();
    private final OptimalNextWordSearcher optimalNextWordSearcher = new OptimalNextWordSearcher();

    private final InputOutputService inputOutputService = new InputOutputService();

    private State state;

    public CliService(Dictionary allWordsDictionary, Dictionary answersDictionary) {
        this.allWordsDictionary = allWordsDictionary;
        this.answersDictionary = answersDictionary;

        this.state = State.createInitialState(answersDictionary.getWords());
    }

    public void run(Action beginning) {
        boolean gameEnded = applyBeginning(beginning);

        while (!gameEnded) {
            Word word = getNextWord();
            gameEnded = applyWord(word);
        }
    }

    private boolean applyBeginning(Action action) {
        for (Word word : action.getWords()) {
            if (applyWord(word)) {
                return true;
            }
        }

        return false;
    }

    private boolean applyWord(Word word) {
        log.info("Applying word \"{}\"", word);

        checkWordExistence(word);
        String lettersStatusString = inputOutputService.readLettersStatus();
        GuessResponse guessResponse = parseGuessResponse(word, lettersStatusString);

        state = engine.doNextStep(state, guessResponse);

        if (state.isAnswerFound() && state.getPossibleAnswers().contains(word)) {
            log.info("Answer is \"{}\", found in {} steps", word, state.getStep());
            return true;
        }

        if (state.getStep() == 6) {
            log.info("Game over, remaining answers: {}", state.getPossibleAnswers().size());
            return true;
        }

        if (state.getPossibleAnswers().isEmpty()) {
            log.info("Cannot find answer, it doesn't present in answers dictionary");
            return true;
        }

        log.info("Current state:\n{}", getShortStateRepresentation());
        return false;
    }

    private void checkWordExistence(Word word) {
        allWordsDictionary.getWords().stream()
            .filter(w -> w.equals(word))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Unknown word for currently used dictionary: " + word));
    }

    private GuessResponse parseGuessResponse(Word word, String lettersStatusString) {
        LetterStatus[] lettersStatus = new LetterStatus[5];

        for (int i = 0; i < lettersStatusString.length(); i++) {
            lettersStatus[i] = switch (lettersStatusString.charAt(i)) {
                case 'E' -> LetterStatus.EXACT_POSITION;
                case 'O' -> LetterStatus.OTHER_POSITION;
                case 'N' -> LetterStatus.NOT_PRESENT;
                default -> throw new IllegalStateException("Unexpected letter status: " + lettersStatusString.charAt(i));
            };
        }

        return new GuessResponse(word.getLetters(), lettersStatus);
    }

    private Word getNextWord() {
        return inputOutputService.readWordOptionally()
            .map(Word::new)
            .orElseGet(
                () -> optimalNextWordSearcher.findNextWord(allWordsDictionary.getWords(), state.getPossibleAnswers())
            );
    }

    private String getShortStateRepresentation() {
        StringBuilder sb = new StringBuilder();

        sb.append("Step: ")
            .append(state.getStep())
            .append('\n')
            .append("Remaining answers: ")
            .append(state.getPossibleAnswers().size())
            .append('/')
            .append(answersDictionary.getWords().size())
            .append(" [");

        String fewAnswers = state.getPossibleAnswers().stream()
            .limit(10)
            .map(Word::getValue)
            .collect(Collectors.joining(", "));

        sb.append(fewAnswers);

        if (state.getPossibleAnswers().size() > 10) {
            sb.append(", ...");
        }

        sb.append(']');

        return sb.toString();
    }
}
