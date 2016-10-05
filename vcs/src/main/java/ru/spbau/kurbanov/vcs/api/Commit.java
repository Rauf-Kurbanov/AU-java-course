package ru.spbau.kurbanov.vcs.api;

public interface Commit {

    int getId();

    int getParentId();

    SnapShot getSnapShot();

    String getMessage();

}
