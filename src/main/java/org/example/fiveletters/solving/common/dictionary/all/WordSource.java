package org.example.fiveletters.solving.common.dictionary.all;

public enum WordSource {
    OPEN_CORPORA,
    HAND_INPUT,
    POISK_SLOV;

    public static WordSource parse(String value) {
        return switch (value) {
            case "OpenCorpora" -> OPEN_CORPORA;
            case "добавлено-руками" -> HAND_INPUT;
            case "поиск-слов.рф" -> POISK_SLOV;
            default -> throw new IllegalStateException("Unexpected word source value: " + value);
        };
    }
}
