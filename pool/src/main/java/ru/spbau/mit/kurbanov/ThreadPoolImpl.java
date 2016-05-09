package ru.spbau.mit.kurbanov;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final Queue<Runnable> tasks = new LinkedList<>();
    private final List<Thread> threads = new ArrayList<>();

    public ThreadPoolImpl(int count) {
        for (int i = 0; i < count; i++) {
            final Worker worker = new Worker(tasks);
            worker.start();
            threads.add(worker);
        }
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