package org.example.fiveletters.solving.engine.dto;

import java.util.EnumMap;
import java.util.Map.Entry;
import lombok.Getter;
import org.example.fiveletters.solving.common.domain.Letter;

@Getter
public class GuessStats {

    private final EnumMap<Letter, LetterStats> letters = new EnumMap<>(Letter.class);

    public void addLetter(Letter letter, int index, LetterStatus status) {
        LetterStats letterStats = letters.computeIfAbsent(letter, l -> new LetterStats());
        letterStats.add(status, index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("GuessStats");

        for (Entry<Letter, LetterStats> entry : letters.entrySet()) {
            sb.append('[')
                .append(entry.getKey().value)
                .append(": ")
                .append(entry.getValue())
                .append(']');
        }

        return sb.toString();
    }
}
