package ru.spbau.kurbanov.vcs.api;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Command {

    protected final Repository repo;

    public abstract void execute();
}
