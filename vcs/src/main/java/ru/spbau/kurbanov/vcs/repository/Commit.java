package ru.spbau.kurbanov.vcs.repository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
public class Commit implements Serializable {

    @Getter
    private final int id;

    @Getter
    private final int parentId;

    @Getter
    private final SnapShot snapShot;

    @Getter
    private final String message;
}
