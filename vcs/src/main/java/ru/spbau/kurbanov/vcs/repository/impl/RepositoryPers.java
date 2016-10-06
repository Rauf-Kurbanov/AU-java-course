package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.repository.api.RepositoryDef;

import java.io.File;

public class RepositoryPers extends RepositoryDef {

    public RepositoryPers(@NotNull File pwd) {
        super(pwd,
                SnapShotPersFactory.getInstance()
                , CommitImplFactory.getInstance());
    }
}
