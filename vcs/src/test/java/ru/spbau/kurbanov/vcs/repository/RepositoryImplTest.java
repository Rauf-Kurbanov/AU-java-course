package ru.spbau.kurbanov.vcs.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.kurbanov.vcs.api.Repository;

import java.util.Arrays;
import java.util.stream.IntStream;


public class RepositoryImplTest {

    private TemporaryFolder folder= new TemporaryFolder();
    private static Repository repo;
    private String[] fileNames;

    @Before
    public void setUp() throws Exception {
        final int nfiles = 5;
        fileNames = IntStream.range(0, nfiles)
                .mapToObj(x -> String.format("file%d.txt", x))
                .toArray(String[]::new);
        repo = new RepositoryImpl();
    }

    @Test
    public void add() throws Exception {
        Arrays.stream(fileNames).forEach(repo::add);

    }

    @Test
    public void commit() throws Exception {

    }

    @Test
    public void createBranch() throws Exception {

    }

    @Test
    public void removeBranch() throws Exception {

    }

    @Test
    public void checkout() throws Exception {

    }

    @Test
    public void log() throws Exception {

    }

    @Test
    public void merge() throws Exception {

    }

}