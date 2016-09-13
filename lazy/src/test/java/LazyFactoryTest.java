import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;

public class LazyFactoryTest {

    private static volatile int state = 0;
    private static final int initialState = 42;

    private static final Supplier<Integer> STATE_CHANGER = ()-> state++;

    private static class TestNullSupplier<T> implements Supplier<T> {

        private int timesCalled = 0;

        @Override
        public T get() {
            timesCalled++;
            return null;
        }
    }

    @org.junit.Before
    public void setUp() throws Exception {
        state = initialState;
    }

    @org.junit.Test
    public void singleThreaded() throws Exception {
        Lazy<Integer> singleLazy = LazyFactory.singleThreaded(STATE_CHANGER);
        runALot(singleLazy);
        assertEquals(initialState + 1, state);
    }

    @org.junit.Test
    public void multiThreaded() throws Exception {
        Lazy<Integer> lazy = new MultiLazyImpl<>(STATE_CHANGER);
        runALot(lazy);
        assertEquals(initialState + 1, state);
    }

    @org.junit.Test
    public void testGetLockfreeLazy() throws Exception {
        Lazy lazyLock = LazyFactory.lockFree(STATE_CHANGER);
        assertEquals(initialState, state);
        runALot(lazyLock);
        assertEquals(initialState + 1, state);
    }

    @org.junit.Test
    public void testLockFreeLazyNull() throws Exception {
        TestNullSupplier tns = new TestNullSupplier();
        Lazy lazy = LazyFactory.lockFree(tns);
        testNull(lazy, tns);
    }

    @org.junit.Test
    public void testSingleThreadedLazyNull() throws Exception {
        TestNullSupplier tns = new TestNullSupplier();
        Lazy lazy = LazyFactory.singleThreaded(tns);
        runALot(lazy);
        assertEquals(1, tns.timesCalled);
        assertEquals(null, lazy.get());
    }

    @org.junit.Test
    public void testMultiThreadedLazyNull() throws Exception {
        TestNullSupplier tns = new TestNullSupplier();
        Lazy lazy = LazyFactory.multiThreaded(tns);
        testNull(lazy, tns);
    }

    @org.junit.Test
    public void testGetLockfreeLazyNull() throws Exception {
        TestNullSupplier tns = new TestNullSupplier();
        Lazy lazy = LazyFactory.lockFree(tns);
        testNull(lazy, tns);
    }

    private <T> void testNull(Lazy<T> lazy, TestNullSupplier tns) {
        runALot(lazy);
        assertEquals(1, tns.timesCalled);
        assertEquals(null, lazy.get());
    }

    private <T> void runALot(Lazy<T> lazy) {
        final int nthreads = 100;
        final int ncalls= 1000000;

        ExecutorService pool = Executors.newFixedThreadPool(nthreads);
        for (int i = 0; i < ncalls; i++) {
            pool.execute(lazy::get);
        }
    }
}