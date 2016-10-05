package ru.spbau.kurbanov.vcs.repository.impl;

import ru.spbau.kurbanov.vcs.repository.api.*;

public class CommitImplFactory implements CommitFactory {

    private static final CommitFactory INSTANCE = new CommitImplFactory();

    private CommitImplFactory() {}

    public static CommitFactory getInstance() { return INSTANCE; }

    @Override
    public Commit newCommit(int id, Branch fromBranch, String message) {
        return new CommitImpl(id, fromBranch, message);
    }

    @Override
    public Commit mergeCommit(int id, Branch fromBranch, Branch toBranch, String message) {
        return new CommitImpl(id, fromBranch, toBranch, message);
    }
}
