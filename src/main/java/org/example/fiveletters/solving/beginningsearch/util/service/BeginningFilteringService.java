package org.example.fiveletters.solving.beginningsearch.util.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.beginningsearch.util.dto.FilteringResult;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.engine.dto.State;
import org.example.fiveletters.solving.engine.service.FiveLettersEngine;
import org.example.fiveletters.solving.beginningsearch.util.dto.Beginning;

@Slf4j
public class BeginningFilteringService {

    private final Dictionary answersDictionary;
    private final List<Beginning> beginnings;

    private final State initialState;
    private final FiveLettersEngine engine;

    private final AtomicInteger progress = new AtomicInteger(0);

    public BeginningFilteringService(Dictionary answersDictionary, List<Beginning> beginnings) {
        this.answersDictionary = answersDictionary;
        this.beginnings = beginnings;

        this.initialState = State.createInitialState(answersDictionary.getWords());
        this.engine = new FiveLettersEngine();
    }

    public FilteringResult filterBeginnings() {
        logProgress(0, beginnings.size());

        List<CompletableFuture<InternalFilteringResult>> futures = beginnings.stream()
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
            .min(Comparator.comparingInt(InternalFilteringResult::remainingAnswersSum))
            .orElseThrow();

        // Среднее арифметическое = (остаток_1 + ... + остаток_N) / N
        BigDecimal averageRemainingAnswersCount = BigDecimal
            .valueOf(bestBeginning.remainingAnswersSum())
            .setScale(2, RoundingMode.UNNECESSARY)
            .divide(BigDecimal.valueOf(answersDictionary.getWords().size()), RoundingMode.CEILING);

        return new FilteringResult(
            bestBeginning.beginning(),
            averageRemainingAnswersCount,
            bestBeginning.maxRemainingAnswersCount()
        );
    }

    private InternalFilteringResult calculateRemainingAnswersSum(Beginning beginning) {
        int remainingAnswersSum = 0;
        int maxRemainingAnswersCount = 0;

        for (Word answer : answersDictionary.getWords()) {
            State nextState = initialState;

            for (Word word : beginning.getWords()) {
                nextState = engine.doNextStep(nextState, word, answer);
            }

            remainingAnswersSum += nextState.getPossibleAnswers().size();
            maxRemainingAnswersCount = Math.max(maxRemainingAnswersCount, nextState.getPossibleAnswers().size());
        }

        int currentProgress = progress.incrementAndGet();
        logProgress(currentProgress, beginnings.size());

        return new InternalFilteringResult(beginning, remainingAnswersSum, maxRemainingAnswersCount);
    }

    private void logProgress(int i, int allCount) {
        if (i % 100 != 0 && i != allCount) {
            return;
        }

        String message = String.format("Processing progress: %.2f%% (%d/%d)",
                                       ((double) i) / allCount * 100, i, allCount);
        log.info(message);
    }

    private record InternalFilteringResult(
        Beginning beginning,
        int remainingAnswersSum,
        int maxRemainingAnswersCount
    ) {}
}
