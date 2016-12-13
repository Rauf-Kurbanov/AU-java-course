package ru.spbau.mit.kurbanov.functional;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {

    private final ArrayList<Integer> randomIntList = new ArrayList<>();
    private final int randomIntListSize = 100;

    @Before
    public void setUp() throws Exception {
        Random random = new Random();
        final int randomRange = 1000;
        for (int i = 0; i < randomIntListSize; ++i) {
            randomIntList.add(random.nextInt(randomRange));
        }
    }

    @Test
    public void testMap() throws Exception {
        int i = 0;
        for (int mapElement : Collections.map(x -> x*x, randomIntList)) {
            assertTrue(i < randomIntListSize);
            assertEquals(randomIntList.get(i) * randomIntList.get(i), mapElement);
            ++i;
        }
        assertEquals(randomIntListSize, i);
    }

    @Test
    public void testFilter() throws Exception {
        int i = 0;
        for (int afterFilterElement : Collections.filter(Predicate.ALWAYS_TRUE, randomIntList)) {
            assertTrue(i < randomIntListSize);
            assertEquals(afterFilterElement, (int) randomIntList.get(i));
            ++i;
        }
        assertEquals(randomIntListSize, i);
        assertFalse(Collections.filter(Predicate.ALWAYS_FALSE, randomIntList).iterator().hasNext());

        i = 0;
        for (int afterFilterElement : Collections.filter(x -> x % 2 == 0, randomIntList)) {
            while (randomIntList.get(i) % 2 == 1 && i < randomIntListSize) {
                ++i;
            }
            assertTrue(i < randomIntListSize);
            assertEquals(afterFilterElement, (int) randomIntList.get(i));
            ++i;
        }
        while (i < randomIntListSize) {
            assertTrue(randomIntList.get(i) % 2 == 1);
            ++i;
        }
    }

    @Test
    public void testTakeWhile() throws Exception {
        int i = 0;
        for (int takeWhileElement : Collections.takeWhile(x -> x % 2 == 0, randomIntList)) {
            assertTrue(i < randomIntListSize);
            assertTrue(randomIntList.get(i) % 2 == 0);
            assertEquals(takeWhileElement, (int) randomIntList.get(i));
            ++i;
        }

        assertTrue(i == randomIntListSize || randomIntList.get(i) % 2 == 1);
    }

    @Test
    public void testTakeUnless() throws Exception {
        int i = 0;
        for (int takeUntilElement : Collections.takeUnless(x -> x % 2 == 0, randomIntList)) {
            assertTrue(i < randomIntListSize);
            assertTrue(randomIntList.get(i) % 2 == 1);
            assertEquals(takeUntilElement, (int) randomIntList.get(i));
            ++i;
        }

        assertTrue(i == randomIntListSize || randomIntList.get(i) % 2 == 0);
    }

    @Test
    public void testFoldl() throws Exception {
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(new Integer[] {1, 2, 3, 5}));
        Function2<Double, Integer, Double> sum = (l, r) -> l - r;

        assertEquals(-11, Collections.foldl(sum, 0.0, numberList).intValue());
   }

    @Test
    public void testFoldr() throws Exception {
        ArrayList<Integer> numberList = new ArrayList<>(Arrays.asList(new Integer[] {1, 2, 3, 5}));
        Function2<Integer, Double, Double> sum = (l, r) -> l - r;
        assertEquals(-3, Collections.foldr(sum, 0.0, numberList).intValue());
    }
}