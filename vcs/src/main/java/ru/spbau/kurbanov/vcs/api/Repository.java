package ru.spbau.kurbanov.vcs.api;

import java.io.IOException;

public interface Repository {

    void add(String fileName);

    void commit(String message) throws IOException;

    void createBranch(String branchName);

    void removeBranch(String branchName);

    void checkout(String branchName);

    void log();

    void merge(String branchName);
}
