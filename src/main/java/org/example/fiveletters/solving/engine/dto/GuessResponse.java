package org.example.fiveletters.solving.engine.dto;

import lombok.AllArgsConstructor;
import org.example.fiveletters.solving.common.domain.Letter;

@AllArgsConstructor
public class GuessResponse {

    private final Letter[] letters;
    private final LetterStatus[] statuses;

    public Letter getLetterAt(int i) {
        return letters[i];
    }

    public LetterStatus getStatusAt(int i) {
        return statuses[i];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Letter l : letters) {
            sb.append(l.value);
        }

        sb.append(" (");
        for (LetterStatus s : statuses) {
            sb.append(switch (s) {
                case EXACT_POSITION -> 'E';
                case OTHER_POSITION -> 'O';
                case NOT_PRESENT -> 'N';
            });
        }
        sb.append(')');

        return sb.toString();
    }
}
