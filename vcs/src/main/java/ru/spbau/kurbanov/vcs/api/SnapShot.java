package ru.spbau.kurbanov.vcs.api;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.repository.SnapShotSer;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public interface SnapShot {

    void add(@NotNull File file) throws IOException;

    @NotNull
    default boolean isEmpty() {
        return getFiles().isEmpty();
    }

    Map<String, byte[]> getFiles();

    @NotNull
    default Map<String, byte[]> intersection(@NotNull SnapShot other) {
        Set<Map.Entry<String, byte[]>> intersection = new HashSet<>(getFiles().entrySet());
        intersection.retainAll(other.getFiles().entrySet());

        return intersection.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    default SnapShot union(@NotNull SnapShot other) {
        Set<Map.Entry<String, byte[]>> filesUnion = new HashSet<>(getFiles().entrySet());
        filesUnion.addAll(other.getFiles().entrySet());

        return new SnapShotSer(filesUnion);
    }

    default Set<String> diff(@NotNull SnapShot other) {
        return intersection(other).keySet().stream()
                .filter(n -> getFiles().get(n) != other.getFiles().get(n))
                .collect(Collectors.toSet());
    }
}

