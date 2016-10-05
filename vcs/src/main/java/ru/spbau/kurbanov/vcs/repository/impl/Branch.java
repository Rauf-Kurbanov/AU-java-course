package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.spbau.kurbanov.vcs.repository.api.Commit;
import ru.spbau.kurbanov.vcs.repository.api.SnapShot;

import java.io.Serializable;

public class Branch implements Serializable {

    @Getter @Setter
    private Commit currentCommit;

    @Getter
    private String name;

    @Getter @Setter
    private SnapShot currSnapShot = new SnapShotSer();

    public Branch(Commit commit, @NotNull String name) {
        currentCommit = commit;
        this.name = name;
    }

    public void flush() {
        currSnapShot = new SnapShotSer();
    }
}
