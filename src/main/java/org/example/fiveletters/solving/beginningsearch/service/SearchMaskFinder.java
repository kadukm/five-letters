package org.example.fiveletters.solving.beginningsearch.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.domain.Letter;
import org.example.fiveletters.solving.common.domain.Word;
import org.example.fiveletters.solving.common.util.LetterCounter;
import org.example.fiveletters.solving.common.util.MaskUtils;

@Slf4j
public class SearchMaskFinder {

    public List<Integer> findBestSearchMasks(Dictionary dictionary, int wordsCount) {
        List<Integer> result = new ArrayList<>(2);

        List<Letter> lettersSortedByFrequency = getLettersSortedByFrequency(dictionary);

        result.add(findBestAllLettersSearchMask(wordsCount, lettersSortedByFrequency));
        result.add(findBestAllVowelsMask(wordsCount, lettersSortedByFrequency));

        return result;
    }

    private List<Letter> getLettersSortedByFrequency(Dictionary dictionary) {
        LetterCounter counter = new LetterCounter();

        dictionary.getWords().stream()
            .map(Word::getLetters)
            .flatMap(Stream::of)
            .forEach(counter::increase);

        int lettersSum = counter.values().stream()
            .reduce(0, Integer::sum);

        ArrayList<Entry<Letter, Integer>> letterFrequency = new ArrayList<>(counter.entrySet());
        letterFrequency.sort(Collections.reverseOrder(Entry.comparingByValue()));

        String frequenciesMessage = letterFrequency.stream()
            .map(e -> "%s - %d - %.2f%%".formatted(
                e.getKey(), e.getValue(), (double) e.getValue() * 100 / lettersSum)
            )
            .collect(Collectors.joining("\n"));
        log.info("Collected frequencies:\n{}", frequenciesMessage);

        return letterFrequency.stream()
            .map(Entry::getKey)
            .toList();
    }

    private int findBestAllLettersSearchMask(int wordsCount, List<Letter> lettersSortedByFrequency) {
        int result = lettersSortedByFrequency.stream()
            .limit(wordsCount * 5L)
            .map(l -> l.mask)
            .reduce(0, (i1, i2) -> i1 | i2);

        log.info("Found best all letters mask: {}", MaskUtils.maskToString(result));

        return result;
    }

    private int findBestAllVowelsMask(int wordsCount, List<Letter> lettersSortedByFrequency) {
        int result = MaskUtils.VOWELS_MASK;

        int lettersToFindCount = wordsCount * 5 - 9;  // 9 - общее кол-во гласных

        for (Letter letter : lettersSortedByFrequency) {
            if ((letter.mask & MaskUtils.VOWELS_MASK) == letter.mask) {
                continue;
            }

            result |= letter.mask;

            lettersToFindCount--;

            if (lettersToFindCount == 0) {
                break;
            }
        }

        log.info("Found best all vowels mask: {}", MaskUtils.maskToString(result));

        return result;
    }
}
