import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import static junit.framework.TestCase.assertEquals;

public class LazyFactoryTest {

    private static volatile int state = 0;
    private static final int initalState = 42;

    private static final Supplier<String> CANDY_SUPPLIER = ()-> "candy";
    private static final Supplier NULL_SUPPLIER = ()-> null;
    private static final Supplier<Integer> STATE_CHANGER = ()-> state++;

    @org.junit.Before
    public void setUp() throws Exception {
        state = initalState;
    }

    @org.junit.Test
    public void singleThreaded() throws Exception {
        Lazy<Integer> singleLazy = LazyFactory.singleThreaded(STATE_CHANGER);
        assertEquals(initalState, (int) singleLazy.get());
        assertEquals(initalState, (int) singleLazy.get());
        assertEquals(initalState, (int) singleLazy.get());
        assertEquals(initalState, (int) singleLazy.get());
        assertEquals(initalState + 1, state);
    }

    @org.junit.Test
    public void multiThreaded() throws Exception {
        final int nthreads = 1000;
        ExecutorService pool = Executors.newFixedThreadPool(nthreads);
        Lazy<Integer> lazy = new MultiLazyImpl<>(STATE_CHANGER);
        for (int i = 0; i < 1000000; i++) {
            pool.execute(() -> lazy.get());
        }
        System.out.println(state);
        assertEquals(initalState + 1, state);
    }

    @org.junit.Test
    public void testGetLockfreeLazy() throws Exception {
        final int nthreads = 100;
        final int ncalls= 1000000;

        Lazy lazyLock = LazyFactory.lockFree(STATE_CHANGER);
        assertEquals(initalState, state);
        ExecutorService pool = Executors.newFixedThreadPool(nthreads);
        for (int i = 0; i < ncalls; i++) {
            pool.execute(lazyLock::get);
        }
        assertEquals(initalState + 1, state);
    }
}