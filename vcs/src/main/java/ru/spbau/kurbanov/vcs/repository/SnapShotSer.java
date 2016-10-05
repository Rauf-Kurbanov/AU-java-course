package ru.spbau.kurbanov.vcs.repository;

import com.sun.istack.internal.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.spbau.kurbanov.vcs.api.SnapShot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode
public class SnapShotSer implements SnapShot {

    @Getter
    private final Map<String, byte[]> files;

    @Override
    public void add(@NotNull File file) throws IOException {
        final String name = file.getPath();
        final byte[] data = Files.readAllBytes(file.toPath());
        files.put(name, data);
    }

    public SnapShotSer(Set<Map.Entry<String, byte[]>> filesSet) {
        this.files = filesSet.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public SnapShotSer() {
        this(new HashSet<>());
    }
}
