package org.example.fiveletters.solving.engine.dto;

import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import lombok.Getter;
import org.example.fiveletters.solving.common.domain.Letter;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.util.MaskUtils;

public class State {

    @Getter
    private final int step;

    private int letter0mask;
    private int letter1mask;
    private int letter2mask;
    private int letter3mask;
    private int letter4mask;

    private final EnumMap<Letter, Integer> letterMinPossibleCount;
    private final EnumMap<Letter, Integer> letterMaxPossibleCount;

    @Getter
    private List<Word> possibleAnswers;

    private State(List<Word> possibleAnswers) {
        this.step = 0;

        this.letter0mask = MaskUtils.ALL_LETTERS_MASK;
        this.letter1mask = MaskUtils.ALL_LETTERS_MASK;
        this.letter2mask = MaskUtils.ALL_LETTERS_MASK;
        this.letter3mask = MaskUtils.ALL_LETTERS_MASK;
        this.letter4mask = MaskUtils.ALL_LETTERS_MASK;

        this.letterMinPossibleCount = new EnumMap<>(Letter.class);
        this.letterMaxPossibleCount = new EnumMap<>(Letter.class);

        this.possibleAnswers = possibleAnswers;
    }

    private State(State s) {
        this.step = s.step + 1;

        this.letter0mask = s.letter0mask;
        this.letter1mask = s.letter1mask;
        this.letter2mask = s.letter2mask;
        this.letter3mask = s.letter3mask;
        this.letter4mask = s.letter4mask;

        this.letterMinPossibleCount = s.letterMinPossibleCount.clone();
        this.letterMaxPossibleCount = s.letterMaxPossibleCount.clone();

        this.possibleAnswers = s.possibleAnswers;
    }

    public static State createInitialState(List<Word> possibleAnswers) {
        return new State(possibleAnswers);
    }

    public boolean isAnswerFound() {
        return possibleAnswers.size() == 1;
    }

    public State applyGuess(GuessStats guessStats) {
        State nextState = new State(this);
        nextState.applyGuessInternal(guessStats);
        return nextState;
    }

    private void applyGuessInternal(GuessStats guessStats) {
        EnumMap<Letter, LetterStats> letters = guessStats.getLetters();

        for (Entry<Letter, LetterStats> entry : letters.entrySet()) {
            Letter key = entry.getKey();
            LetterStats value = entry.getValue();

            for (int exactIndex : value.getExactPositionIndices()) {
                handleExactPosition(key, exactIndex);
            }

            for (int otherIndex : value.getOtherPositionIndices()) {
                handleOtherPosition(key, otherIndex);
            }

            int currentLetterCount = value.getExactPositionIndices().size() + value.getOtherPositionIndices().size();
            if (currentLetterCount != 0) {
                letterMinPossibleCount.merge(key, currentLetterCount, Math::max);
            }

            handleNotPresentPosition(key, value);
        }

        filterPossibleAnswers();
    }

    private void handleExactPosition(Letter letter, int index) {
        switch (index) {
            case 0 -> letter0mask &= letter.mask;
            case 1 -> letter1mask &= letter.mask;
            case 2 -> letter2mask &= letter.mask;
            case 3 -> letter3mask &= letter.mask;
            case 4 -> letter4mask &= letter.mask;
        }
    }

    private void handleOtherPosition(Letter letter, int index) {
        switch (index) {
            case 0 -> letter0mask = deleteLetterFromMask(letter0mask, letter.mask);
            case 1 -> letter1mask = deleteLetterFromMask(letter1mask, letter.mask);
            case 2 -> letter2mask = deleteLetterFromMask(letter2mask, letter.mask);
            case 3 -> letter3mask = deleteLetterFromMask(letter3mask, letter.mask);
            case 4 -> letter4mask = deleteLetterFromMask(letter4mask, letter.mask);
        }
    }

    private void handleNotPresentPosition(Letter letter, LetterStats stats) {
        if (stats.getNotPresentIndices().isEmpty()) {
            return;
        }

        if (stats.getOtherPositionIndices().isEmpty()) {
            for (int i = 0; i < 5; i++) {
                if (stats.getExactPositionIndices().contains(i)) {
                    continue;
                }

                switch (i) {
                    case 0 -> letter0mask = deleteLetterFromMask(letter0mask, letter.mask);
                    case 1 -> letter1mask = deleteLetterFromMask(letter1mask, letter.mask);
                    case 2 -> letter2mask = deleteLetterFromMask(letter2mask, letter.mask);
                    case 3 -> letter3mask = deleteLetterFromMask(letter3mask, letter.mask);
                    case 4 -> letter4mask = deleteLetterFromMask(letter4mask, letter.mask);
                }
            }
        } else {
            for (int noIndex : stats.getNotPresentIndices()) {
                switch (noIndex) {
                    case 0 -> letter0mask = deleteLetterFromMask(letter0mask, letter.mask);
                    case 1 -> letter1mask = deleteLetterFromMask(letter1mask, letter.mask);
                    case 2 -> letter2mask = deleteLetterFromMask(letter2mask, letter.mask);
                    case 3 -> letter3mask = deleteLetterFromMask(letter3mask, letter.mask);
                    case 4 -> letter4mask = deleteLetterFromMask(letter4mask, letter.mask);
                }
            }
        }

        letterMaxPossibleCount.put(letter, letterMinPossibleCount.getOrDefault(letter, 0));
    }

    private int deleteLetterFromMask(int currentMask, int letterMask) {
        return currentMask & (currentMask ^ letterMask);
    }

    private void filterPossibleAnswers() {
        possibleAnswers = possibleAnswers.stream()
            .filter(this::applicableToWord)
            .toList();
    }

    private boolean applicableToWord(Word word) {
        if ((letter0mask & word.getMaskAt(0)) != word.getMaskAt(0)) {
            return false;
        }
        if ((letter1mask & word.getMaskAt(1)) != word.getMaskAt(1)) {
            return false;
        }
        if ((letter2mask & word.getMaskAt(2)) != word.getMaskAt(2)) {
            return false;
        }
        if ((letter3mask & word.getMaskAt(3)) != word.getMaskAt(3)) {
            return false;
        }
        if ((letter4mask & word.getMaskAt(4)) != word.getMaskAt(4)) {
            return false;
        }

        int knownLettersCount = letterMinPossibleCount.values().stream()
            .reduce(Integer::sum)
            .orElse(0);
        for (Entry<Letter, Integer> entry : word.getLetterCounter().entrySet()) {
            Letter key = entry.getKey();
            Integer value = entry.getValue();

            int minPossibleCount = letterMinPossibleCount.getOrDefault(key, 0);

            if (value < minPossibleCount) {
                return false;
            }

            if (value > letterMaxPossibleCount.getOrDefault(key, 5 - knownLettersCount + minPossibleCount)) {
                return false;
            }
        }

        for (Letter key : letterMinPossibleCount.keySet()) {
            if (!word.getLetterCounter().containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("                   | а б в г д е ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я |\n");
        sb.append("                    -----------------------------------------------------------------\n");

        for (int i = 0; i < 5; i++) {
            sb.append("                 ").append(i).append(" | ");

            int stateLetterMask = switch (i) {
                case 0 -> letter0mask;
                case 1 -> letter1mask;
                case 2 -> letter2mask;
                case 3 -> letter3mask;
                case 4 -> letter4mask;
                default -> throw new IllegalStateException("Unexpected value: " + i);
            };
            for (Letter l : Letter.values()) {

                if (l.mask == (stateLetterMask & l.mask)) {
                    sb.append(' ');
                } else {
                    sb.append('X');
                }
                sb.append(' ');
            }
            sb.append("|\n");
        }

        sb.append("                    -----------------------------------------------------------------\n");

        sb.append("max possible count | ");
        for (Letter l : Letter.values()) {
            Integer value = letterMaxPossibleCount.get(l);
            if (value == null) {
                sb.append(' ');
            } else {
                sb.append(value);
            }
            sb.append(' ');
        }
        sb.append("|\n");

        sb.append("min possible count | ");
        for (Letter l : Letter.values()) {
            Integer value = letterMinPossibleCount.get(l);
            if (value == null) {
                sb.append(' ');
            } else {
                sb.append(value);
            }
            sb.append(' ');
        }
        sb.append("|\n");

        return sb.toString();
    }
}
