package org.example.fiveletters.solving.common.util;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.Set;
import org.example.fiveletters.solving.common.domain.Letter;

public class LetterCounter {

    private final EnumMap<Letter, Integer> map;

    public LetterCounter() {
        this.map = new EnumMap<>(Letter.class);
    }

    public LetterCounter(EnumMap<Letter, Integer> map) {
        this.map = new EnumMap<>(map);
    }

    public boolean containsKey(Letter key) {
        return map.containsKey(key);
    }

    public void increase(Letter l) {
        map.merge(l, 1, Integer::sum);
    }

    public boolean tryDecrease(Letter l) {
        Integer value = map.get(l);
        if (value == null) {
            return false;
        }

        if (value == 1) {
            map.remove(l);
        } else {
            map.put(l, value - 1);
        }

        return true;
    }

    public Set<Letter> keySet() {
        return map.keySet();
    }

    public Collection<Integer> values() {
        return map.values();
    }

    public Set<Entry<Letter, Integer>> entrySet() {
        return map.entrySet();
    }

    public LetterCounter copy() {
        return new LetterCounter(map);
    }
}
