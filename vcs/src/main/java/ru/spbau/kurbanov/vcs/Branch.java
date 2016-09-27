package ru.spbau.kurbanov.vcs;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Branch implements Serializable {

    @Getter @Setter
    private Commit currentCommit;

    @Getter
    private String name;

    public Branch(Commit commit, String name) {
        currentCommit = commit;
        this.name = name;
    }
}
