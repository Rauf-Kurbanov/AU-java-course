package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.organicdesign.fp.collections.PersistentTreeMap;
import ru.spbau.kurbanov.vcs.repository.api.SnapShotDefault;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@EqualsAndHashCode
public class SnapShotPers implements SnapShotDefault {

    @Getter
    private PersistentTreeMap<String, byte[]> files = PersistentTreeMap.empty();

    @Override
    public void add(@NotNull File file) throws IOException {
        final String name = file.getPath();
        final byte[] data = Files.readAllBytes(file.toPath());
        files = files.assoc(name, data);
    }
}
