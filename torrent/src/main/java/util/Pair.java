package util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor
public class Pair<K, V> {
    @Getter
    private final K key;
    @Getter
    private final V value;
}
