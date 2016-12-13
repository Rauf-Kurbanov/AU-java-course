package ru.spbau.kurbanov.vcs.repository.api;

import com.sun.istack.internal.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

public interface SnapShot {

    void add(@NotNull File file) throws IOException;

    @NotNull
    boolean isEmpty();

    Map<String, byte[]> getFiles();

    @NotNull
    Map<String, byte[]> intersection(@NotNull SnapShot other);

    SnapShotDefault union(@NotNull SnapShot other);

    Set<String> diff(@NotNull SnapShot other);
}
