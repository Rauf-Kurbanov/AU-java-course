package ru.spbau.mit.kurbanov;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class ThreadPoolImplTest {

    @Test
    public void testAdd() throws Exception {

        final Integer[] counter = {0};

        final Supplier<Integer> incrementer = () -> {
            synchronized (counter) {
                counter[0] += 1;
                return 1;
            }
        };

        final ThreadPool pool = new ThreadPoolImpl(10);
        final Queue<LightFuture<Integer>> results = new LinkedList<>();
        final int SUM = 1000000;
        for (int i = 0; i < SUM; ++i) {
            results.add(pool.add(incrementer));
        }

        int actualResult = 0;
        while (!results.isEmpty()) {
            LightFuture<Integer> result = results.poll();
            actualResult += result.get();
        }

        pool.shutdown();

        assertEquals(SUM, actualResult);
        assertEquals(SUM, (int) counter[0]);
    }

    @Test
    public void testThreadCount() throws InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(10);
        final Barrier barrier = new Barrier(10);

        final int[] result = {0};

        for (int i = 0; i < 9; i++) {
            pool.add(() -> {
                try {
                    barrier.await();
                } catch (InterruptedException ignored) {}

                result[0]++;
                return 0;
            });
        }

        barrier.await();
        pool.shutdown();

        assertEquals(9, result[0]);
    }

    @Test
    public void testThenApply() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(10);

        int result = pool
                .add(() -> 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> i + 1)
                .thenApply((i) -> -i)
                .get();

        pool.shutdown();

        assertEquals(-8, result);
    }

    @Test(expected = LightExecutionException.class)
    public void checkExceptions() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(10);

        pool.add(() -> {
            throw new IllegalStateException();
        }).get();

        pool.shutdown();
    }

    private final class Barrier {
        private final int parties;
        private int currentCount = 0;

        public Barrier(int parties) {
            this.parties = parties;
        }

        public synchronized void await() throws InterruptedException {
            currentCount++;

            if (currentCount == parties) { notifyAll(); }
            while (currentCount < parties) { wait();}
        }
    }
}