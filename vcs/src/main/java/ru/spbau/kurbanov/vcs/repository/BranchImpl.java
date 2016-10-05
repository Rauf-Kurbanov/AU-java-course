package ru.spbau.kurbanov.vcs.repository;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.spbau.kurbanov.vcs.api.Branch;
import ru.spbau.kurbanov.vcs.api.Commit;
import ru.spbau.kurbanov.vcs.api.SnapShot;

import java.io.Serializable;

public class BranchImpl implements Branch, Serializable {

    @Getter @Setter
    private Commit currentCommit;

    @Getter
    private String name;

    @Getter @Setter
    private SnapShot currSnapShot = new SnapShotSer();

    public BranchImpl(Commit commit, @NotNull String name) {
        currentCommit = commit;
        this.name = name;
    }

    @Override
    public void flush() {
        currSnapShot = new SnapShotSer();
    }
}
