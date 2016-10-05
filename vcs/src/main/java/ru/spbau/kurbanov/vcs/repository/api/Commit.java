package ru.spbau.kurbanov.vcs.repository.api;

import java.io.Serializable;

public interface Commit extends Serializable {

    int getId();

    int getParentId();

    SnapShot getSnapShot();

    String getMessage();

}
