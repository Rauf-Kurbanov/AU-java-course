package ru.spbau.kurbanov.vcs;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
public class SnapShot implements Serializable {

    public SnapShot(Set<File> files) {
        this.files = files;
    }

    @Getter
    private Set<File> files = new HashSet<>();

    public void add(File file) {
        files.add(file);
    }

    @NotNull
    public Set<File> intersection(SnapShot other) {
        Set<File> intersection = new HashSet<>(files);
        intersection.retainAll(other.files);

        return intersection;
    }

    public SnapShot union(SnapShot other) {
        Set<File> filesUnion = new HashSet<>(files);
        filesUnion.addAll(other.files);

        return new SnapShot(filesUnion);
    }

    public boolean isEmpty() { return files.isEmpty(); }
}
