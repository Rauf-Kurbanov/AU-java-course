package ru.spbau.mit.kurbanov;

import java.util.Queue;

class Worker extends Thread {
    private final Queue<Runnable> tasks;

    public Worker(Queue<Runnable> tasks) {
        this.tasks = tasks;
    }

    public void run() {
        Runnable task;
        try {
            while (!isInterrupted()) {

                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        tasks.wait();
                    }
                    task = tasks.poll();
                }
                task.run();
            }
        } catch (InterruptedException ignored) {}
    }
}