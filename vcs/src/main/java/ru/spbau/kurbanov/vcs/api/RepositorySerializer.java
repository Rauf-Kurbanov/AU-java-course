package ru.spbau.kurbanov.vcs.api;

import java.io.IOException;

public interface RepositorySerializer {

    // TODO consider getting rid of exceptions
    void serialize(Repository repo) throws IOException;

    Repository deserialize();
}
