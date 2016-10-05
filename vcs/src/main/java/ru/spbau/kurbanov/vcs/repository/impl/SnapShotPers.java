package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import org.organicdesign.fp.collections.PersistentTreeMap;
import ru.spbau.kurbanov.vcs.repository.api.SnapShotDefault;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Map;

public class SnapShotPers implements SnapShotDefault, Serializable {

//    @Getter
    private final PersistentTreeMap<String, byte[]> files = PersistentTreeMap.empty();

    @Override
    public void add(@NotNull File file) throws IOException {
        final String name = file.getPath();
        final byte[] data = Files.readAllBytes(file.toPath());
        files.assoc(name, data);
    }

    @Override
    public Map<String, byte[]> getFiles() {
        return null;
    }
}
