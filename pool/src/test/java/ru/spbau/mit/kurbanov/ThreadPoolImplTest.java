package ru.spbau.mit.kurbanov;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ThreadPoolImplTest {

    private final int N_THREAD = 10;
    
    @Test
    public void testAdd() throws Exception {

        final Integer[] counter = {0};

        final Supplier<Integer> incrementer = () -> {
            synchronized (counter) {
                counter[0] += 1;
                return 1;
            }
        };

        final ThreadPool pool = new ThreadPoolImpl(N_THREAD);
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
        final ThreadPool pool = new ThreadPoolImpl(N_THREAD);
        final Barrier barrier = new Barrier(N_THREAD);

        final int[] result = {0};
        
        Stream
                .iterate(0, x -> x + 1)
                .limit(9)
                .peek(i -> pool.add(() -> {
                    try {
                        barrier.await();
                    } catch (InterruptedException ignored) {}

                    result[0]++;
                    return 0;
                }))
                .collect(Collectors.toList());

        barrier.await();
        pool.shutdown();

        assertEquals(9, result[0]);
    }

    @Test
    public void testThenApply() throws LightExecutionException, InterruptedException {
        final ThreadPool pool = new ThreadPoolImpl(N_THREAD);

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
        final ThreadPool pool = new ThreadPoolImpl(N_THREAD);

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