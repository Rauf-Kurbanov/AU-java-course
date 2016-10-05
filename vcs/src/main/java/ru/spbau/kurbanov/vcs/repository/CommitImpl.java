package ru.spbau.kurbanov.vcs.repository;

import lombok.Getter;
import ru.spbau.kurbanov.vcs.api.Branch;
import ru.spbau.kurbanov.vcs.api.Commit;
import ru.spbau.kurbanov.vcs.api.SnapShot;

import java.io.Serializable;

//@RequiredArgsConstructor
public class CommitImpl implements Commit, Serializable {

    @Getter
    private final int id;

    @Getter
    private final int parentId;

    @Getter
    private final SnapShot snapShot;

//    private final Branch fromBranch;

//    public void save(@NotNull File metaFolder) throws IOException {
//        final String folderName = String.format("revision-%d", id);
//        File revisionFolder = new File(metaFolder, folderName);
//        revisionFolder.mkdir();
//
//        for (File f : snapShot.getFiles()) {
//            FileUtils.copyFileToDirectory(f, revisionFolder);
//        }
//    }

    @Getter
    private final String message;

//    public CommitImpl(int id, int parentId, Branch fromBranch, String message) {
//    public CommitImpl(int id, int parentId, Branch fromBranch, String message) {
//        this.id = id;
//        this.parentId = parentId;
//        this.message = message;
//        this.fromBranch = fromBranch;
//        this.snapShot = fromBranch.getCurrSnapShot();
//         TODO
//        fromBranch.setCurrSnapShot(new SnapShotSer());
//    }

////    public CommitImpl(int id, int parentId, Branch fromBranch, SnapShot snapShot, String message) {
//    public CommitImpl(int id, int parentId, SnapShot snapShot, String message) {
//        this.id = id;
//        this.parentId = parentId;
//        this.message = message;
////        this.fromBranch = fromBranch;
//        this.snapShot = snapShot;
////        fromBranch.setCurrSnapShot(new SnapShotSer());
//    }

    //    public CommitImpl(int id, int parentId, Branch fromBranch, SnapShot snapShot, String message) {
    public CommitImpl(int id, Branch fromBranch, String message) {
        this.id = id;
        this.parentId  = fromBranch.getCurrentCommit() == null ? id : fromBranch.getCurrentCommit().getId();

//        this.parentId = fromBranch.getCurrentCommit().getId();
        this.message = message;
//        this.fromBranch = fromBranch;
        if (fromBranch.getCurrentCommit() == null) {
            this.snapShot = fromBranch.getCurrSnapShot();
        } else {
            this.snapShot = fromBranch.getCurrentCommit().getSnapShot().union(fromBranch.getCurrSnapShot());
        }
//        fromBranch.setCurrSnapShot(new SnapShotSer());
        fromBranch.flush();
    }

    //merge commit
    public CommitImpl(int id, Branch fromBranch, Branch toBranch, String message) {
        this.id = id;
        if (fromBranch.getCurrentCommit() == null && toBranch.getCurrSnapShot() == null) {
            this.snapShot = new SnapShotSer();
        } else if (fromBranch.getCurrentCommit() == null) {
            this.snapShot = toBranch.getCurrentCommit().getSnapShot();
        } else if (toBranch.getCurrentCommit() == null) {
            this.snapShot = fromBranch.getCurrentCommit().getSnapShot();
        } else {
            this.snapShot = fromBranch.getCurrentCommit().getSnapShot()
                    .union(toBranch.getCurrentCommit().getSnapShot());
        }
        this.parentId  = fromBranch.getCurrentCommit() == null ? id : fromBranch.getCurrentCommit().getId();
        this.message = message;

        fromBranch.flush();
        toBranch.flush();
    }

}
