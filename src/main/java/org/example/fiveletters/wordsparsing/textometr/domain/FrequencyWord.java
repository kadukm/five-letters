package org.example.fiveletters.wordsparsing.textometr.domain;

public record FrequencyWord(
    String word,
    double frequency
) implements Comparable<FrequencyWord> {

    @Override
    public int compareTo(FrequencyWord o) {
        if (frequency < o.frequency) {
            return 1;
        }

        if (frequency > o.frequency) {
            return -1;
        }

        return word.compareTo(o.word);
    }
}
