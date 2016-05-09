package ru.spbau.mit.kurbanov;

import java.util.function.Supplier;

public interface ThreadPool {

    <R> LightFuture<R> add(Supplier<R> task);

    void shutdown() throws InterruptedException;
}