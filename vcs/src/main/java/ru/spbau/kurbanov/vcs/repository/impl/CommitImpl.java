package ru.spbau.kurbanov.vcs.repository.impl;

import lombok.Getter;
import ru.spbau.kurbanov.vcs.repository.api.Commit;
import ru.spbau.kurbanov.vcs.repository.api.SnapShot;

//@RequiredArgsConstructor
public class CommitImpl implements Commit {

    @Getter
    private final int id;

    @Getter
    private final int parentId;

    @Getter
    private final SnapShot snapShot;

    @Getter
    private final String message;

    public CommitImpl(int id, Branch fromBranch, String message) {
        this.id = id;
        this.parentId  = fromBranch.getCurrentCommit() == null ? id : fromBranch.getCurrentCommit().getId();

        this.message = message;
        if (fromBranch.getCurrentCommit() == null) {
            this.snapShot = fromBranch.getCurrSnapShot();
        } else {
            this.snapShot = fromBranch.getCurrentCommit().getSnapShot().union(fromBranch.getCurrSnapShot());
        }
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
