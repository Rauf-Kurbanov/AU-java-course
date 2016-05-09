package ru.spbau.mit.kurbanov;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Function;

public class LightFuture<T> {

    private final ThreadPoolImpl threadPool;
    private final Queue<Runnable> waiting = new LinkedList<>();
    private T result;
    private LightExecutionException leException;

    private void flushWaiting() {
        synchronized (waiting) {
            waiting.forEach(threadPool::addTask);
            waiting.clear();
        }
    }

    public LightFuture(ThreadPoolImpl threadPool) {
        this.threadPool = threadPool;
    }

    public <R> LightFuture<R> thenApply(Function<? super T, R> function) {
        final LightFuture<R> result = new LightFuture<>(threadPool);
        final Runnable task = () -> {
            try {
                final T value = get();
                result.setResult(function.apply(value));
            } catch (Exception e) {
                result.setLeException(e);
            }
        };

        if (!isReady()) {
            waiting.add(task);
        } else {
            threadPool.addTask(task);
        }

        return result;
    }

    public synchronized T get() throws LightExecutionException, InterruptedException {
        while (!isReady()) {
            wait();
        }

        if (leException != null) {
            throw leException;
        }

        return result;
    }

    public boolean isReady() {
        return (result != null || leException != null);
    }

    public synchronized void setResult(T result) {
        this.result = result;
        notify();

        flushWaiting();
    }


    public synchronized void setLeException(Exception e) {
        this.leException = new LightExecutionException(e);
        notify();

        flushWaiting();
    }
}