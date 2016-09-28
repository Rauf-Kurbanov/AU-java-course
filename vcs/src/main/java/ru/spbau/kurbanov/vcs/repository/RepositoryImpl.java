package ru.spbau.kurbanov.vcs.repository;

import com.sun.istack.internal.NotNull;
import org.apache.commons.io.FileUtils;
import ru.spbau.kurbanov.vcs.api.Repository;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class RepositoryImpl implements Repository, Serializable {

    private static final String META_DIR_NAME = ".vcs";
    private static final String MASTER = "master";

    // TODO let Commit to handle counter by himself
    private final ArrayList<Commit> allCommits = new ArrayList<>();

    private SnapShot currSnapShot = new SnapShot();

    private HashMap<String, Branch> branches = new HashMap<>();

    private Branch head;

    public RepositoryImpl() {
        Commit inital = new Commit(-1, -1, null, "inital commit");
        branches.put(MASTER, new Branch(inital, MASTER));
        head = branches.get(MASTER);

        File vcsDir = new File(META_DIR_NAME);
        if (!vcsDir.exists()) {
            vcsDir.mkdir();
        }
    }

    @Override
    public void add(@NotNull String fileName) {
        File inpFile = new File(fileName);
        if (!inpFile.exists()) {
            System.out.format("fatal: pathspec '%s' did not match any files", fileName);
            return;
        }
        currSnapShot.add(inpFile);
    }

    @Override
    public void commit(@NotNull String message) throws IOException {
        if (currSnapShot.isEmpty()) {
            System.out.println("nothing added to commit");
            return;
        }
        final int revisionNum = allCommits.size();

        int prevCommitId = head.getCurrentCommit() == null ? -1 : head.getCurrentCommit().getId();
        int currCommitId = allCommits.size();

        Commit newCommit = new Commit(currCommitId, prevCommitId, currSnapShot, message);
        allCommits.add(newCommit);
        head.setCurrentCommit(newCommit);
        currSnapShot = new SnapShot();

        final String folderName = String.format("revision-%d", revisionNum);
        File revisionFolder = new File(folderName);
        revisionFolder.mkdir();

        for (File f : newCommit.getSnapShot().getFiles()) {
            FileUtils.copyFileToDirectory(f, revisionFolder);
        }
    }

    @Override
    public void createBranch(@NotNull String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.format("fatal: A branch named %s already exists.", branchName);
            return;
        }
        Branch newBracnh = new Branch(head.getCurrentCommit(), branchName);
        branches.put(branchName, newBracnh);
    }

    @Override
    public void removeBranch(@NotNull String branchName) {
        if (head.getName().equals(branchName)) {
            System.out.printf("Cannot delete the branch '%s' which you are currently on.", branchName);
            return;
        }
        branches.remove(branchName);
    }

    @Override
    public void checkout(@NotNull String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.printf("error: pathspec '%s' did not match any file(s) known to vcs.", branchName);
            return;
        }
        head = branches.get(branchName);
    }

    @Override
    public void log() {
        Commit commitToLog = head.getCurrentCommit();
        while (commitToLog.getId() != -1) {
            System.out.println("commit:");
            System.out.printf("id: %d", commitToLog.getId());
            System.out.printf("message: %s\n", commitToLog.getMessage());

            commitToLog = allCommits.get(commitToLog.getParentId());
        }
        System.out.println("commit:");
        System.out.printf("id: %d", commitToLog.getId());
        System.out.printf("message: %s\n", commitToLog.getMessage());
    }

    @Override
    public void merge(@NotNull String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.printf("error: pathspec '%s' did not match any file(s) known to vcs.", branchName);
            return;
        }
        Commit thisCommit = head.getCurrentCommit();
        Commit otherCommit = branches.get(branchName).getCurrentCommit();

        SnapShot thisSnapShot = thisCommit.getSnapShot();
        SnapShot otherSnapShot = otherCommit.getSnapShot();

        Commit mergeCommit = null;
        final String commitMessage =  String.format("Merging branch %s to %s", branchName, head.getName());

        if (thisSnapShot.intersection(otherSnapShot).size() == 0) {
            mergeCommit = new Commit(allCommits.size(),
                    head.getCurrentCommit().getId(),
                    thisSnapShot.union(otherSnapShot),
                    commitMessage);
        } else {
            // hande conflicts
            // for now latest commit always overwrites previous one
            Commit latest = thisCommit.getId() > otherCommit.getId() ? thisCommit : otherCommit;
            mergeCommit = new Commit(allCommits.size(),
                    thisCommit.getId(),
                    latest.getSnapShot(),
                    commitMessage);
        }
        allCommits.add(mergeCommit);
        head.setCurrentCommit(mergeCommit);
    }
}
