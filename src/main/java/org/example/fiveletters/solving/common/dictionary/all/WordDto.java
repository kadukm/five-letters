package org.example.fiveletters.solving.common.dictionary.all;

public record WordDto(
    String value,
    WordSource source,
    Boolean exists
) { }
