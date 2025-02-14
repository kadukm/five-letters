package org.example.fiveletters.solving.common.engine.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.State;

@Slf4j
public class ActionFilteringService {

    private final List<Word> possibleAnswers;
    private final List<Action> actions;

    private final State initialState;
    private final FiveLettersEngine engine;

    private final AtomicInteger progress = new AtomicInteger(0);

    public ActionFilteringService(List<Word> possibleAnswers, List<Action> actions) {
        this.possibleAnswers = possibleAnswers;
        this.actions = actions;

        this.initialState = State.createInitialState(this.possibleAnswers);
        this.engine = new FiveLettersEngine();
    }

    public FilteringResult filterActions() {
        logProgress(0, actions.size());

        List<CompletableFuture<InternalFilteringResult>> futures = actions.stream()
            .map(b -> CompletableFuture.supplyAsync(() -> calculateRemainingAnswersSum(b)))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        InternalFilteringResult bestBeginning = futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            })
            .min(createComparatorByRemainingAnswersSum())
            .orElseThrow();

        // Среднее арифметическое = (остаток_1 + ... + остаток_N) / N
        BigDecimal averageRemainingAnswersCount = BigDecimal
            .valueOf(bestBeginning.remainingAnswersSum())
            .setScale(2, RoundingMode.UNNECESSARY)
            .divide(BigDecimal.valueOf(possibleAnswers.size()), RoundingMode.CEILING);

        return new FilteringResult(
            bestBeginning.action(),
            averageRemainingAnswersCount,
            bestBeginning.maxRemainingAnswersCount()
        );
    }

    private InternalFilteringResult calculateRemainingAnswersSum(Action beginning) {
        int remainingAnswersSum = 0;
        int maxRemainingAnswersCount = 0;

        for (Word answer : possibleAnswers) {
            State nextState = initialState;

            for (Word word : beginning.getWords()) {
                nextState = engine.doNextStep(nextState, word, answer);
            }

            remainingAnswersSum += nextState.getPossibleAnswers().size();
            maxRemainingAnswersCount = Math.max(maxRemainingAnswersCount, nextState.getPossibleAnswers().size());
        }

        int currentProgress = progress.incrementAndGet();
        logProgress(currentProgress, actions.size());

        return new InternalFilteringResult(beginning, remainingAnswersSum, maxRemainingAnswersCount);
    }

    private void logProgress(int i, int allCount) {
        if (i % 100 != 0 && i != allCount) {
            return;
        }

        String message = String.format("Processing progress: %.2f%% (%d/%d)",
                                       ((double) i) / allCount * 100, i, allCount);
        log.debug(message);
    }

    private Comparator<InternalFilteringResult> createComparatorByRemainingAnswersSum() {
        return Comparator
            .comparingInt(InternalFilteringResult::remainingAnswersSum)
            .thenComparing(
                (r1, r2) -> {
                    boolean r1InAnswers = r1.action().getWords().stream()
                        .anyMatch(possibleAnswers::contains);
                    boolean r2InAnswers = r2.action().getWords().stream()
                        .anyMatch(possibleAnswers::contains);

                    if (r1InAnswers && !r2InAnswers) {
                        return -1;
                    }
                    if (!r1InAnswers && r2InAnswers) {
                        return 1;
                    }

                    return 0;
                }
            );
    }

    private record InternalFilteringResult(
        Action action,
        int remainingAnswersSum,
        int maxRemainingAnswersCount
    ) { }
}
