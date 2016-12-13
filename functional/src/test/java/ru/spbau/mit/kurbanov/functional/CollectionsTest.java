package ru.spbau.mit.kurbanov.functional;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class CollectionsTest {

    private static final int RANDOM_INT_LIST_SIZE = 100;
    private static final ArrayList<Integer> RANDOM_INT_LIST = new ArrayList<>();
    private static final ArrayList<Integer> PI_LIST =
            new ArrayList<>(Arrays.asList(new Integer[] {3, 1, 4, 1, 5, 9, 2, 6, 5, 4}));
    private static final Predicate<Number> WILD_EVEN_PRED = x -> x.intValue() % 2 == 0;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Random random = new Random();
        final int randomRange = 1000;
        for (int i = 0; i < RANDOM_INT_LIST_SIZE; ++i) {
            RANDOM_INT_LIST.add(random.nextInt(randomRange));
        }
    }

    @Test
    public void testMap() throws Exception {
        int i = 0;
        Function1<Number, Integer> mapper = x -> x.intValue() * x.intValue() ;
        for (int mapElement : Collections.map(mapper, RANDOM_INT_LIST)) {
            assertTrue(i < RANDOM_INT_LIST_SIZE);
            assertEquals(RANDOM_INT_LIST.get(i) * RANDOM_INT_LIST.get(i), mapElement);
            i++;
        }
        assertEquals(RANDOM_INT_LIST_SIZE, i);

        List<Integer> res = Collections.map(mapper, PI_LIST);
        List<Integer> expected = new ArrayList<>(Arrays.asList(new Integer[] {9, 1, 16, 1, 25, 81, 4, 36, 25, 16}));
        assertEquals(expected, res);
    }

    @Test
    public void testFilter() throws Exception {
        int i = 0;
        for (int afterFilterElement : Collections.filter(Predicate.ALWAYS_TRUE, RANDOM_INT_LIST)) {
            assertTrue(i < RANDOM_INT_LIST_SIZE);
            assertEquals(afterFilterElement, (int) RANDOM_INT_LIST.get(i));
            ++i;
        }
        assertEquals(RANDOM_INT_LIST_SIZE, i);
        assertFalse(Collections.filter(Predicate.ALWAYS_FALSE, RANDOM_INT_LIST).iterator().hasNext());

        i = 0;
        for (int afterFilterElement : Collections.filter(WILD_EVEN_PRED, RANDOM_INT_LIST)) {
            while (RANDOM_INT_LIST.get(i) % 2 == 1 && i < RANDOM_INT_LIST_SIZE) {
                ++i;
            }
            assertTrue(i < RANDOM_INT_LIST_SIZE);
            assertEquals(afterFilterElement, (int) RANDOM_INT_LIST.get(i));
            ++i;
        }
        while (i < RANDOM_INT_LIST_SIZE) {
            assertTrue(RANDOM_INT_LIST.get(i) % 2 == 1);
            ++i;
        }

        List<Integer> res = Collections.filter(WILD_EVEN_PRED, PI_LIST);
        List<Integer> expected = new ArrayList<>(Arrays.asList(new Integer[] {4, 2, 6, 4}));
        assertEquals(expected, res);
    }

    @Test
    public void testTakeWhile() throws Exception {
        int i = 0;
        for (int takeWhileElement : Collections.takeWhile(WILD_EVEN_PRED, RANDOM_INT_LIST)) {
            assertTrue(i < RANDOM_INT_LIST_SIZE);
            assertTrue(RANDOM_INT_LIST.get(i) % 2 == 0);
            assertEquals(takeWhileElement, (int) RANDOM_INT_LIST.get(i));
            ++i;
        }
        assertTrue(i == RANDOM_INT_LIST_SIZE || RANDOM_INT_LIST.get(i) % 2 == 1);

        List<Integer> res = Collections.takeWhile(x -> x < 9, PI_LIST);
        List<Integer> expected = new ArrayList<>(Arrays.asList(new Integer[] {3, 1, 4, 1, 5}));
        assertEquals(expected, res);
    }

    @Test
    public void testTakeUnless() throws Exception {
        int i = 0;
        for (int takeUntilElement : Collections.takeUnless(WILD_EVEN_PRED, RANDOM_INT_LIST)) {
            assertTrue(i < RANDOM_INT_LIST_SIZE);
            assertTrue(RANDOM_INT_LIST.get(i) % 2 == 1);
            assertEquals(takeUntilElement, (int) RANDOM_INT_LIST.get(i));
            ++i;
        }
        assertTrue(i == RANDOM_INT_LIST_SIZE || RANDOM_INT_LIST.get(i) % 2 == 0);

        List<Integer> res = Collections.takeUnless(x -> x >= 9, PI_LIST);
        List<Integer> expected = new ArrayList<>(Arrays.asList(new Integer[] {3, 1, 4, 1, 5}));
        assertEquals(expected, res);
    }

    @Test
    public void testFoldl() throws Exception {
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(new Integer[] {1, 2, 3, 5}));
        Function2<Double, Number, Double> minus = (l, r) -> l - r.intValue();
        assertEquals(-11, Collections.foldl(minus, 0.0, numberList).intValue());
    }

    @Test
    public void testFoldr() throws Exception {
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(new Integer[] {1, 2, 3, 5}));
        Function2<Number, Double, Double> minus = (l, r) -> l.intValue() - r;
        assertEquals(-3, Collections.foldr(minus, 0.0, numberList).intValue());
    }
}