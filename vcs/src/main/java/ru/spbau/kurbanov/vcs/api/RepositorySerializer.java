package ru.spbau.kurbanov.vcs.api;

import java.io.File;
import java.io.IOException;

public interface RepositorySerializer {

    void serialize(Repository repo) throws IOException;

    Repository deserialize(File path);

}
