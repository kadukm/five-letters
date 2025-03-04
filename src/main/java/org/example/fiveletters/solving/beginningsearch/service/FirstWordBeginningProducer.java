package org.example.fiveletters.solving.beginningsearch.service;

import java.util.List;
import org.example.fiveletters.solving.common.dictionary.Dictionary;
import org.example.fiveletters.solving.common.engine.dto.Action;

public class FirstWordBeginningProducer {

    public static List<Action> produce(Dictionary dictionary) {
        return dictionary.getWords().stream()
            .map(w -> new Action(List.of(w)))
            .toList();
    }
}
