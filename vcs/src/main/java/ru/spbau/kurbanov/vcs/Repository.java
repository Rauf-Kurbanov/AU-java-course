package ru.spbau.kurbanov.vcs;

import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Repository implements Serializable {

    private static final String META_DIR_NAME = ".vcs";
    private static final String MASTER = "master";
    private static final String SERIALIZED_PATH = ".vcs/repo.ser";

    // TODO let Commit to handle counter by himself
    private final ArrayList<Commit> allCommits = new ArrayList<>();

    @Getter
    private SnapShot currSnapShot = new SnapShot();

    private HashMap<String, Branch> branches = new HashMap<>();
    @Getter
    private Branch head;

    // TODO refactor do u need private and final
    @Getter
    private final String pwd = System.getProperty("user.dir");

    // TODO use instance everywhere
    private static Repository instance = new Repository();

    // TODO stop throwing out of constructor
    public void init() {
        try {
            if (deserialize()) {
                return;
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        instance = new Repository();
    }

    private Repository() {
        Commit inital = new Commit(-1, -1, null, "inital commit");
        branches.put(MASTER, new Branch(inital, MASTER));
        head = branches.get(MASTER);

        File vcsDir = new File(getPwd(), META_DIR_NAME);
        if (!vcsDir.exists()) {
            vcsDir.mkdir();
        }
    }

    public void add(File file) {
        currSnapShot.add(file);
    }

    public void commit(String message) throws IOException {
        if (getCurrSnapShot().isEmpty()) {
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

    public void createBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            System.out.format("fatal: A branch named %s already exists.", branchName);
            return;
        }
        Branch newBracnh = new Branch(head.getCurrentCommit(), branchName);
        branches.put(branchName, newBracnh);
    }

    public void removeBranch(String branchName) {
        if (head.getName().equals(branchName)) {
            System.out.printf("Cannot delete the branch '%s' which you are currently on.", branchName);
            return;
        }
        branches.remove(branchName);
    }

    public void checkout(String branchName) {
        if (!branches.containsKey(branchName)) {
            System.out.printf("error: pathspec '%s' did not match any file(s) known to vcs.", branchName);
            return;
        }
        head = branches.get(branchName);
    }

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

    public void merge(String branchName) {
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

    public void serialize() throws IOException {
        FileOutputStream fileOut =
                new FileOutputStream(SERIALIZED_PATH);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    private static boolean deserialize() throws IOException {
        if (new File(SERIALIZED_PATH).exists()) {
            try {
                FileInputStream fileIn = new FileInputStream(SERIALIZED_PATH);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                instance = (Repository) in.readObject();
                in.close();
                fileIn.close();
            } catch(ClassNotFoundException c) {
                System.out.println("Repository class not found");
                return true;
            }
        }
        return false;
    }
}
