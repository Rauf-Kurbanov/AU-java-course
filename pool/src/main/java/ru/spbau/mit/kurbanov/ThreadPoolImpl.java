package ru.spbau.mit.kurbanov;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ThreadPoolImpl implements ThreadPool {

    private final Queue<Runnable> tasks = new LinkedList<>();
    private final List<Thread> threads = new ArrayList<>();

    public ThreadPoolImpl(int count) {
        Stream
                .iterate(0, x -> x + 1)
                .limit(count)
                .map(i -> new Worker(tasks))
                .peek(Worker::start)
                .forEach(threads::add);
    }

    public void addTask(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    @Override
    public <T> LightFuture<T> add(Supplier<T> task) {
        final LightFuture<T> result = new LightFuture<>(this);

        synchronized (tasks) {
            tasks.add(() -> {
                try {
                    result.setResult(task.get());
                } catch (Exception e) {
                    result.setLeException(e);
                }
            });

            tasks.notify();
        }

        return result;
    }

    @Override
    public void shutdown() throws InterruptedException {
        for (Thread thread : threads) {
            thread.interrupt();
            thread.join();
        }
    }
}