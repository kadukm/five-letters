package org.example.fiveletters.solving.research.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.engine.dto.Action;
import org.example.fiveletters.solving.common.engine.dto.State;
import org.example.fiveletters.solving.common.engine.service.FiveLettersEngine;
import org.example.fiveletters.solving.common.engine.service.OptimalNextWordSearcher;
import org.example.fiveletters.solving.research.dto.GameStats;
import org.example.fiveletters.solving.research.dto.SummaryStats;

@Slf4j
public class StatsCalculator {

    private final Dictionary allWordsDictionary;
    private final Dictionary answersDictionary;

    private final FiveLettersEngine engine = new FiveLettersEngine();
    private final OptimalNextWordSearcher optimalNextWordSearcher = new OptimalNextWordSearcher();

    private final Word answer;
    private State state;
    private boolean inProgress;

    private final AtomicInteger progress;

    private StatsCalculator(Dictionary allWordsDictionary, Dictionary answersDictionary, Word answer,
                            AtomicInteger progress) {
        this.allWordsDictionary = allWordsDictionary;
        this.answersDictionary = answersDictionary;

        this.answer = answer;
        this.state = State.createInitialState(answersDictionary.getWords());
        this.inProgress = true;

        this.progress = progress;
    }

    public static SummaryStats calculate(Dictionary allWordsDictionary, Dictionary answersDictionary,
                                         Action beginning) {
        AtomicInteger progress = new AtomicInteger(0);
        logProgress(0, answersDictionary.getWords().size());

        List<CompletableFuture<GameStats>> futures = answersDictionary.getWords().stream()
            .map(answer -> new StatsCalculator(allWordsDictionary, answersDictionary, answer, progress))
            .map(calculator -> CompletableFuture.supplyAsync(() -> calculator.runGame(beginning)))
            .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<GameStats> gamesStats = futures.stream()
            .map(f -> {
                try {
                    return f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            })
            .toList();

        return collectSummary(beginning, gamesStats);
    }

    private static SummaryStats collectSummary(Action beginning, List<GameStats> gamesStats) {
        BigDecimal averageStepsSpentCount = BigDecimal
            .valueOf(
                gamesStats.stream()
                    .map(GameStats::stepsSpentCount)
                    .reduce(0, Integer::sum)
            )
            .setScale(4, RoundingMode.UNNECESSARY)
            .divide(BigDecimal.valueOf(gamesStats.size()), RoundingMode.CEILING);

        long lostGamesCount = gamesStats.stream()
            .filter(stats -> !stats.isWon())
            .count();

        TreeMap<Integer, Integer> stepsStats = gamesStats.stream()
            .collect(
                Collectors.toMap(
                    GameStats::stepsSpentCount,
                    gs -> 1,
                    Integer::sum,
                    TreeMap::new
                )
            );

        return new SummaryStats(beginning, averageStepsSpentCount, (int) lostGamesCount, stepsStats);
    }

    private GameStats runGame(Action beginning) {
        applyBeginning(beginning);

        while (inProgress) {
            Word word = optimalNextWordSearcher.findNextWord(allWordsDictionary.getWords(), state.getPossibleAnswers());
            applyWord(word);
        }

        int currentProgress = progress.incrementAndGet();
        logProgress(currentProgress, answersDictionary.getWords().size());

        return new GameStats(state.getStep(), state.getStep() <= 6);
    }

    private void applyBeginning(Action action) {
        for (Word word : action.getWords()) {
            applyWord(word);
            if (!inProgress) {
                return;
            }
        }
    }

    private void applyWord(Word word) {
        state = engine.doNextStep(state, word, answer);

        if (state.isAnswerFound() && state.getPossibleAnswers().contains(word)) {
            inProgress = false;
        }
    }

    private static void logProgress(int i, int allCount) {
        if (i % 50 != 0 && i != allCount) {
            return;
        }

        String message = String.format("Calculating progress: %.2f%% (%d/%d)",
                                       ((double) i) / allCount * 100, i, allCount);
        log.info(message);
    }
}
