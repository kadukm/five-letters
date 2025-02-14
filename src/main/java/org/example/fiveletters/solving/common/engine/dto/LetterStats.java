package org.example.fiveletters.solving.common.engine.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class LetterStats {

    private final List<Integer> exactPositionIndices = new ArrayList<>(3);
    private final List<Integer> otherPositionIndices = new ArrayList<>(3);
    private final List<Integer> notPresentIndices = new ArrayList<>(3);

    public void add(LetterStatus status, int index) {
        switch (status) {
            case EXACT_POSITION -> exactPositionIndices.add(index);
            case OTHER_POSITION -> otherPositionIndices.add(index);
            case NOT_PRESENT -> notPresentIndices.add(index);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        boolean isFirst = true;

        for (int i : exactPositionIndices) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }

            sb.append("E-").append(i);
        }
        for (int i : otherPositionIndices) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }

            sb.append("O-").append(i);
        }
        for (int i : notPresentIndices) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(", ");
            }

            sb.append("N-").append(i);
        }

        return sb.toString();
    }
}
