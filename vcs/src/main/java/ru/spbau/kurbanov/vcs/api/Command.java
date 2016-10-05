package ru.spbau.kurbanov.vcs.api;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public abstract class Command {

    protected final Repository repo;

    public abstract void execute() throws IOException;
}
