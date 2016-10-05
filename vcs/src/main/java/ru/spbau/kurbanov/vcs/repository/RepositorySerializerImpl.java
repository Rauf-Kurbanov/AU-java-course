package ru.spbau.kurbanov.vcs.repository;

import com.sun.istack.internal.NotNull;
import ru.spbau.kurbanov.vcs.api.Repository;
import ru.spbau.kurbanov.vcs.api.RepositorySerializer;

import java.io.*;

public class RepositorySerializerImpl implements RepositorySerializer {

    public final String SERIALIZED_PATH = ".vcs/repo.ser";

    @Override
    public void serialize(@NotNull Repository repo) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(SERIALIZED_PATH);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)
        ) {
            out.writeObject(repo);
        }
    }

    @Override
    public Repository deserialize(File path) {
        if (!new File(SERIALIZED_PATH).exists()) {
            System.out.format("Can't find file %s", SERIALIZED_PATH);
            return new RepositoryImpl(path);
        }
        try (FileInputStream fileIn = new FileInputStream(SERIALIZED_PATH);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            return (Repository) in.readObject();
        } catch (IOException e) {
            System.out.format("Can't open file %s", SERIALIZED_PATH);
        } catch (ClassNotFoundException e) {
            System.out.println("Cant find serialized Repository object");
        }
        return new RepositoryImpl(path);
    }
}
