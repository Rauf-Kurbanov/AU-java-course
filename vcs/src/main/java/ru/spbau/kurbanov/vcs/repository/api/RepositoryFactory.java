package ru.spbau.kurbanov.vcs.repository.api;

import ru.spbau.kurbanov.vcs.repository.impl.RepositorySer;
import ru.spbau.kurbanov.vcs.repository.impl.RepositorySerializer;

import java.io.File;
import java.io.IOException;

public class RepositoryFactory {

    private static final RepositorySerializer rs = new RepositorySerializer();

    public static Repository initRepo(File path) {
        if (!new File(rs.SERIALIZED_PATH).exists()) {
            System.out.format("Can't find file %s", rs.SERIALIZED_PATH);
            return new RepositorySer(path);
        }

        try {
            return rs.deserialize(path);
        } catch (IOException e) {
            System.out.format("Can't open file %s", rs.SERIALIZED_PATH);
        } catch (ClassNotFoundException e) {
            System.out.println("Cant find serialized Repository object");
        }
        return new RepositorySer(path);
    }

}
