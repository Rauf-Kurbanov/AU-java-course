package ru.spbau.kurbanov.vcs.repository.impl;

import ru.spbau.kurbanov.vcs.repository.api.SnapShot;
import ru.spbau.kurbanov.vcs.repository.api.SnapShotFactory;

public class SnapShotPersFactory implements SnapShotFactory {

    private static final SnapShotFactory INSTANCE = new SnapShotPersFactory();

    private SnapShotPersFactory() {}

    public static SnapShotFactory getInstance() {
        return INSTANCE;
    }

    private final SnapShot emptySnapShot = new SnapShotPers();


    @Override
    public SnapShot emptySnapshot() {
        return emptySnapShot;
    }
}
