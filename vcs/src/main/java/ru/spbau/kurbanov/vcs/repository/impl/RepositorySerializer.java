package ru.spbau.kurbanov.vcs.repository.impl;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.repository.api.Repository;

import java.io.*;

public class RepositorySerializer {

    public static final String SERIALIZED_PATH = ".vcs/repo.ser";

    public static void serialize(@NotNull Repository repo) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(SERIALIZED_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(repo);
        }
    }

    public static Repository deserialize(File path) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(SERIALIZED_PATH);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Repository) in.readObject();
        }
    }
}
