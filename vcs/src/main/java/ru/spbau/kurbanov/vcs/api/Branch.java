package ru.spbau.kurbanov.vcs.api;

public interface Branch {

    Commit getCurrentCommit();

    void setCurrentCommit(Commit commit);

    String getName();

    SnapShot getCurrSnapShot();

    void setCurrSnapShot(SnapShot snapShot);

    void flush();
}
