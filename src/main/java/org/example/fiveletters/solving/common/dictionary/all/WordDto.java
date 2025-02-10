package org.example.fiveletters.solving.common.dictionary.all;

public record WordDto(String value, WordSource source, Boolean exists) {

    public static WordDto parseFromCsvLine(String line) {
        String[] csvValues = line.split(";");

        WordSource source = WordSource.parse(csvValues[1]);

        Boolean exists = null;
        if (csvValues.length > 2) {
            exists = switch (csvValues[2]) {
                case "exists" -> Boolean.TRUE;
                case "not-exists" -> Boolean.FALSE;
                default -> throw new IllegalStateException("Unexpected word existing value: " + exists);
            };
        }

        return new WordDto(csvValues[0], source, exists);
    }
}
