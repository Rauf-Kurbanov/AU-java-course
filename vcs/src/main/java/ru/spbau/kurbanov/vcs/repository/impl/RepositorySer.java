package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.repository.api.RepositoryDef;

import java.io.File;

public class RepositorySer extends RepositoryDef {

    public RepositorySer(@NotNull File pwd) {
        super(pwd,
                SnapShotSerFactory.getInstance(),
                CommitImplFactory.getInstance());
    }
}
