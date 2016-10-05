package ru.spbau.kurbanov.vcs.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import ru.spbau.kurbanov.vcs.repository.impl.RepositorySer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;


public class RepositorySerTest {
    public TemporaryFolder tmpFolder;
    public File projRoot;

    private static RepositorySer repo;
    private String[] fileNames;

    @Before
    public void setUp() throws Exception {
        tmpFolder = new TemporaryFolder();
        tmpFolder.create();
        projRoot = tmpFolder.getRoot();

        final int nfiles = 5;
        fileNames = IntStream.range(0, nfiles)
                .mapToObj(x -> String.format("file%d.txt", x))
                .toArray(String[]::new);

        for (String fn : fileNames) {
            tmpFolder.newFile(fn).createNewFile();
        }

        repo = new RepositorySer(projRoot);
    }

    @Test
    public void initalState() {
        assertThat(repo.getBranches().size(), is(equalTo(1)));
        assertThat(repo.getAllCommits().size(), is(equalTo(0)));
        assertTrue(repo.getHead().getCurrSnapShot().isEmpty());
        assertTrue(new File(projRoot, repo.META_DIR_NAME).exists());
    }

    @Test
    public void add() throws IOException {
        for (String name : fileNames) {
            name = new File(projRoot, name).getName();
            repo.add(name);
        }
        assertFalse(repo.getHead().getCurrSnapShot().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void commitEmpty() throws Exception {
        repo.commit("must fail");
    }

    @Test
    public void commit() throws Exception {
        final int nCommit = 0;
        repo.add(new File(fileNames[nCommit]).toString());
        repo.commit("one file commit");

        assertTrue(repo.getHead().getCurrSnapShot().isEmpty());
    }

    @Test
    public void createBranch() throws Exception {
        final String name = "somename";
        repo.createBranch(name);
        assertTrue(repo.getBranches().containsKey(name));
    }

    @Test
    public void removeBranch() throws Exception {
        final String name = "somename";
        repo.createBranch(name);
        repo.removeBranch(name);
        assertFalse(repo.getBranches().containsKey(name));
    }

    @Test
    public void checkout() throws Exception {
        final String name = "somename";
        repo.createBranch(name);
        repo.checkout(name);
        assertThat(repo.getHead().getName(), is(equalTo(name)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkoutDontExist() {
        repo.checkout("dont exist");
    }

    @Test
    public void checkoutNonEmptyIndex() throws IOException {
        repo.add(fileNames[0]);
        repo.createBranch("A");
        repo.checkout("A");
        repo.add(fileNames[1]);
        assertThat(repo.getHead().getCurrSnapShot().getFiles().size()
                , is(equalTo(1)));
        repo.checkout("master");
        assertThat(repo.getHead().getCurrSnapShot().getFiles().size()
                , is(equalTo(1)));
    }

    @Test
    public void log() throws Exception {
        repo.add("file0.txt");
        repo.commit("first commit");
        repo.add("file2.txt");
        repo.add("file3.txt");
        repo.commit("second commit");
        repo.createBranch("new branch");
        repo.checkout("new branch");
        repo.add("file4.txt");
        repo.commit("last commit");
        repo.checkout("master");
        repo.add("file1.txt");
        repo.commit("one more");

        String s = repo.log();
        assertThat(s, is(equalTo(
                String.format("commit:\n" +
                        "id: 4\n" +
                        "message: one more\n" +
                        "commit:\n" +
                        "id: 2\n" +
                        "message: second commit\n" +
                        "commit:\n" +
                        "id: 1\n" +
                        "message: first commit\n"))));
    }

    @Test
    public void mergeSucceed() throws Exception {
        repo.add(fileNames[0]);
        repo.commit("first commit");

        repo.createBranch("other_branch");

        repo.add(fileNames[1]);
        repo.commit("second commit");

        repo.checkout("other_branch");
        repo.add(fileNames[2]);
        repo.commit("A");
        repo.add(fileNames[3]);
        repo.commit("B");

        repo.checkout("master");
        repo.add(fileNames[4]);
        repo.commit("both");

        repo.checkout("other_branch");
        repo.merge("master");

        Set<String> actualFilesExt = repo.getHead().getCurrentCommit().getSnapShot()
                .getFiles().keySet();
        Set<String> actualFiles = new HashSet<>();
        for (String p : actualFilesExt) {
            String[] s = p.split("/");
            actualFiles.add(s[s.length - 1]);
        }
        assertThat(actualFiles, containsInAnyOrder(fileNames));
    }

    @Test(expected = IllegalStateException.class)
    public void mergeFail() throws Exception {
        repo.add(fileNames[0]);
        repo.commit("first commit");

        repo.createBranch("other_branch");

        repo.add(fileNames[1]);
        repo.commit("second commit");

        repo.checkout("other_branch");

        byte data[] = {1, 1, 1, 0, 1, 1};
        try (FileOutputStream out = new FileOutputStream(fileNames[0])) {
            out.write(data);
        }
        repo.add(fileNames[0]);
        repo.commit("overwriting");
        repo.merge("master");
    }
}