package ru.spbau.kurbanov.vcs.repository.api;

import ru.spbau.kurbanov.vcs.repository.impl.Branch;

public interface CommitFactory {

    Commit newCommit(int id, Branch fromBranch, String message);

    Commit mergeCommit(int id, Branch fromBranch, Branch toBranch, String message);
}
