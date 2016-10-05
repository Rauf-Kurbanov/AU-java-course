package ru.spbau.kurbanov.vcs.repository.impl;

import ru.spbau.kurbanov.vcs.repository.api.SnapShot;
import ru.spbau.kurbanov.vcs.repository.api.SnapShotFactory;

public final class SnapShotSerFactory implements SnapShotFactory {

    private static final SnapShotFactory INSTANCE = new SnapShotSerFactory();

    private SnapShotSerFactory() {}

    public static SnapShotFactory getInstance() {
        return INSTANCE;
    }

    private final SnapShot emptySnapShot = new SnapShotSer();

    @Override
    public SnapShot emptySnapshot() {
        return emptySnapShot;
    }
}
