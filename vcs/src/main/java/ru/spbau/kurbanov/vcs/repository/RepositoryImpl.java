package ru.spbau.kurbanov.vcs.repository;

import com.sun.istack.internal.NotNull;
import lombok.Getter;
import ru.spbau.kurbanov.vcs.api.Branch;
import ru.spbau.kurbanov.vcs.api.Commit;
import ru.spbau.kurbanov.vcs.api.Repository;
import ru.spbau.kurbanov.vcs.api.SnapShot;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class RepositoryImpl implements Repository, Serializable {

    private final File pwd;
    public final String META_DIR_NAME = ".vcs";
    private static final String MASTER = "master";

    @Getter
    private final ArrayList<Commit> allCommits = new ArrayList<>();


    @Getter
    private HashMap<String, Branch> branches = new HashMap<>();

    @Getter
    private Branch head;

    public RepositoryImpl(@NotNull File pwd) {
        this.pwd = pwd;

        branches.put(MASTER, new BranchImpl(null, MASTER));
        head = branches.get(MASTER);

        File vcsDir = new File(pwd, META_DIR_NAME);
        if (!vcsDir.exists()) { vcsDir.mkdir(); }

    }

    @Override
    public void add(@NotNull String fileName) throws IOException {
        File inpFile = new File(pwd, fileName);
        checkArgument(inpFile.exists(),
                String.format("fatal: pathspec '%s' did not match any files", fileName));

        head.getCurrSnapShot().add(inpFile);
    }

    @Override
    public void commit(@NotNull String message) throws IOException {
        checkState(!head.getCurrSnapShot().isEmpty(), "nothing added to commit");

        final int currCommitId = allCommits.size();

        Commit newCommit = new CommitImpl(currCommitId, head, message);
        allCommits.add(newCommit);
        head.setCurrentCommit(newCommit);
        head.setCurrSnapShot(new SnapShotSer());
    }

    @Override
    public void createBranch(@NotNull String branchName) {
        checkArgument(!branches.containsKey(branchName),
                String.format("fatal: A branch named %s already exists.", branchName));

        Branch newBranch = new BranchImpl(head.getCurrentCommit(), branchName);
        branches.put(branchName, newBranch);
    }

    @Override
    public void removeBranch(@NotNull String branchName) {
        checkState(!head.getName().equals(branchName),
                String.format("Cannot delete the branch '%s' which you are currently on.", branchName));
        branches.remove(branchName);
    }

    @Override
    public void checkout(@NotNull String branchName) {
        checkArgument(branches.containsKey(branchName),
                String.format("error: pathspec '%s' did not match any file(s) known to vcs.", branchName));
        head = branches.get(branchName);
    }

    @Override
    public String log() {
        StringBuilder sb = new StringBuilder();

        Commit commitToLog = head.getCurrentCommit();
        while (commitToLog.getParentId() != commitToLog.getId()) {
            sb.append(String.format("commit:\nid: %d\nmessage: %s\n",
                    commitToLog.getId() + 1, commitToLog.getMessage()));

            // TODO refactor to getPrevious
            commitToLog = allCommits.get(commitToLog.getParentId());
        }
        sb.append(String.format("commit:\nid: %d\nmessage: %s\n",
                commitToLog.getId() + 1, commitToLog.getMessage()));
        return sb.toString();
    }

    @Override
    public void merge(@NotNull String branchName) {
        checkArgument(branches.containsKey(branchName),
                String.format("error: pathspec '%s' did not match any file(s) known to vcs.", branchName));

        final Branch otherBranch = branches.get(branchName);
        final Commit thisCommit = head.getCurrentCommit();
        Commit otherCommit = otherBranch.getCurrentCommit();

        SnapShot thisSnapShot = thisCommit.getSnapShot();
        SnapShot otherSnapShot = otherCommit.getSnapShot();

        final String conflictFilenames = thisSnapShot
                .intersection(otherSnapShot)
                .keySet()
                .stream()
                .collect(Collectors.joining(", "));
        checkState(thisSnapShot.diff(otherSnapShot).isEmpty()
                , String.format("Can't merge. Merge conflict on files: %s", conflictFilenames));

        Commit mergeCommit = null;
        final String commitMessage = String.format("Merging branch %s to %s", branchName, head.getName());

        // TODO
        if (thisSnapShot.diff(otherSnapShot).isEmpty()) {
            mergeCommit = new CommitImpl(allCommits.size(),
                    head,
                    otherBranch,
                    commitMessage);

        } else {
            Branch latest = thisCommit.getId() > otherCommit.getId() ? head : otherBranch;
            mergeCommit = new CommitImpl(allCommits.size(),
                    latest,
                    commitMessage);
        }
        allCommits.add(mergeCommit);
        head.setCurrentCommit(mergeCommit);
    }
}
