package org.example.fiveletters.solving.engine.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.State;
import org.example.fiveletters.solving.common.engine.service.FiveLettersEngine;
import org.junit.jupiter.api.Test;

public class FiveLettersEngineTest {

    @Test
    void happyPath() {
        Word guess = new Word("абвер");
        Word answer = new Word("абака");
        State state = State.createInitialState(List.of(guess, answer));

        State nextState = new FiveLettersEngine().doNextStep(state, guess, answer);

        assertThat('\n' + nextState.toString()).isEqualTo('\n' +
            """
                               | а б в г д е ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я |
                                -----------------------------------------------------------------
                             0 |   X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X |
                             1 | X   X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X |
                             2 |     X     X                     X                               |
                             3 |     X     X                     X                               |
                             4 |     X     X                     X                               |
                                -----------------------------------------------------------------
            max possible count |     0     0                     0                               |
            min possible count | 1 1                                                             |
            """
        );
        assertThat(nextState.getPossibleAnswers()).containsExactly(answer);
    }

    @Test
    void sameWordTwice() {
        Word guess = new Word("абвер");
        Word answer = new Word("абака");

        State state = State.createInitialState(List.of(guess, answer));
        FiveLettersEngine engine = new FiveLettersEngine();

        State nextState = engine.doNextStep(state, guess, answer);
        nextState = engine.doNextStep(nextState, guess, answer);

        assertThat('\n' + nextState.toString()).isEqualTo('\n' +
            """
                               | а б в г д е ж з и й к л м н о п р с т у ф х ц ч ш щ ъ ы ь э ю я |
                                -----------------------------------------------------------------
                             0 |   X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X |
                             1 | X   X X X X X X X X X X X X X X X X X X X X X X X X X X X X X X |
                             2 |     X     X                     X                               |
                             3 |     X     X                     X                               |
                             4 |     X     X                     X                               |
                                -----------------------------------------------------------------
            max possible count |     0     0                     0                               |
            min possible count | 1 1                                                             |
            """
        );
        assertThat(nextState.getPossibleAnswers()).containsExactly(answer);
    }
}
