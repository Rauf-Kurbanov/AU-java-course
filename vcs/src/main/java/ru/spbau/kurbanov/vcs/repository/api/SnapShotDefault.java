package ru.spbau.kurbanov.vcs.repository.api;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.repository.impl.SnapShotSer;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface SnapShotDefault extends SnapShot, Serializable {

    @Override
    @NotNull
    default boolean isEmpty() {
        return getFiles().isEmpty();
    }

    @Override
    @NotNull
    default Map<String, byte[]> intersection(@NotNull SnapShot other) {
        Set<Map.Entry<String, byte[]>> intersection = new HashSet<>(getFiles().entrySet());
        intersection.retainAll(other.getFiles().entrySet());

        return intersection.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    default SnapShotDefault union(@NotNull SnapShot other) {
        Set<Map.Entry<String, byte[]>> filesUnion = new HashSet<>(getFiles().entrySet());
        filesUnion.addAll(other.getFiles().entrySet());

        return new SnapShotSer(filesUnion);
    }

    @Override
    default Set<String> diff(@NotNull SnapShot other) {
        return intersection(other).keySet().stream()
                .filter(n -> getFiles().get(n) != other.getFiles().get(n))
                .collect(Collectors.toSet());
    }
}

