package org.example.fiveletters.solving.common.util;

public class MaskUtils {

    public static final int ALL_LETTERS_MASK = 0b11111111111111111111111111111111;
    public static final int VOWELS_MASK;

    static {
        VOWELS_MASK = instantiateVowelsMask();
    }

    private static int instantiateVowelsMask() {
        return LetterUtils.VOWELS.stream()
            .map(l -> l.mask)
            .reduce(0, (i1, i2) -> i1 | i2);
    }

    private MaskUtils() {
        throw new UnsupportedOperationException();
    }

    public static String maskToString(int mask) {
        return "%32s".formatted(Integer.toBinaryString(mask)).replace(' ', '0');
    }
}
