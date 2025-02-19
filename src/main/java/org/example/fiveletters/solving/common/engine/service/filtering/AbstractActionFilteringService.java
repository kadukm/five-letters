package org.example.fiveletters.solving.common.engine.service.filtering;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.FilteringResult;
import org.example.fiveletters.solving.common.engine.dto.State;
import org.example.fiveletters.solving.common.engine.service.FiveLettersEngine;
import org.slf4j.event.Level;

@Slf4j
public abstract class AbstractActionFilteringService {

    protected final Set<Word> possibleAnswers;
    protected final List<Action> actions;

    protected final Level logLevel;

    protected final State initialState;
    protected final FiveLettersEngine engine;

    protected final AtomicInteger progress = new AtomicInteger(0);

    protected AbstractActionFilteringService(Set<Word> possibleAnswers, List<Action> actions, Level logLevel) {
        this.possibleAnswers = possibleAnswers;
        this.actions = actions;

        this.logLevel = logLevel;

        this.initialState = State.createInitialState(this.possibleAnswers);
        this.engine = new FiveLettersEngine();
    }

    protected Stream<InternalFilteringResult> handleActions() {
        logProgress(0, actions.size());

        List<CompletableFuture<InternalFilteringResult>> futures = actions.stream()
            .map(action -> CompletableFuture.supplyAsync(() -> handleAction(action)))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    protected InternalFilteringResult handleAction(Action action) {
        InternalFilteringResult result = calculateInternalFilteringResult(action);

        int currentProgress = progress.incrementAndGet();
        logProgress(currentProgress, actions.size());

        return result;
    }

    protected InternalFilteringResult calculateInternalFilteringResult(Action action) {
        int remainingAnswersSum = 0;
        int maxRemainingAnswersCount = 0;

        for (Word answer : possibleAnswers) {
            State nextState = initialState;

            for (Word word : action.getWords()) {
                nextState = engine.doNextStep(nextState, word, answer);
            }

            // Если для какого-то ответа мы не получили никакой новой информации,
            // значит рассматриваемое продолжение точо не является лучшим
            if (nextState.getPossibleAnswers().size() == initialState.getPossibleAnswers().size()) {
                return new InternalFilteringResult(action, Integer.MAX_VALUE, Integer.MAX_VALUE);
            }

            remainingAnswersSum += nextState.getPossibleAnswers().size();
            maxRemainingAnswersCount = Math.max(maxRemainingAnswersCount, nextState.getPossibleAnswers().size());
        }

        return new InternalFilteringResult(action, remainingAnswersSum, maxRemainingAnswersCount);
    }

    protected FilteringResult mapInternalFilteringResult(InternalFilteringResult internalFilteringResult) {
        return new FilteringResult(
            internalFilteringResult.action(),
            calculateAverageRemainingAnswersCount(internalFilteringResult),
            internalFilteringResult.maxRemainingAnswersCount()
        );
    }

    protected BigDecimal calculateAverageRemainingAnswersCount(InternalFilteringResult internalFilteringResult) {
        // Среднее арифметическое = (остаток_1 + ... + остаток_N) / N
        return BigDecimal
            .valueOf(internalFilteringResult.remainingAnswersSum())
            .setScale(4, RoundingMode.UNNECESSARY)
            .divide(BigDecimal.valueOf(possibleAnswers.size()), RoundingMode.CEILING);
    }

    protected Comparator<InternalFilteringResult> createComparatorByRemainingAnswersSum() {
        return Comparator
            .comparingInt(InternalFilteringResult::remainingAnswersSum)
            .thenComparing(createComparatorByAnswerInAction())
            .thenComparingInt(InternalFilteringResult::maxRemainingAnswersCount);
    }

    private Comparator<InternalFilteringResult> createComparatorByAnswerInAction() {
        return (r1, r2) -> {
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
        };
    }

    private void logProgress(int i, int allCount) {
        if (i % 100 != 0 && i != allCount) {
            return;
        }

        String message = String.format("Processing progress: %.2f%% (%d/%d)",
                                       ((double) i) / allCount * 100, i, allCount);
        log.makeLoggingEventBuilder(logLevel)
            .log(message);
    }

    protected record InternalFilteringResult(
        Action action,
        int remainingAnswersSum,
        int maxRemainingAnswersCount
    ) { }
}
