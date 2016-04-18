package ru.spbau.mit.kurbanov.functional;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    private static final Predicate<Integer> DIV2 = x -> x % 2 ==0;
    private static final Predicate<Integer> DIV5 = x -> x % 5 ==0;

    @Test
    public void testOr() throws Exception {
        assertTrue(DIV2.or(DIV5).apply(5));
        assertTrue(DIV2.or(DIV5).apply(4));
        assertTrue(DIV2.or(DIV5).apply(6));
        assertFalse(DIV2.or(DIV5).apply(17));
    }

    @Test
    public void testAnd() throws Exception {
        assertTrue(DIV2.and(DIV5).apply(10000000));
        assertFalse(DIV2.and(DIV5).apply(444));
        assertFalse(DIV2.and(DIV5).apply(555));
        assertFalse(DIV2.and(DIV5).apply(222));
    }

    @Test
    public void testNot() throws Exception {
        assertTrue(Predicate.ALWAYS_FALSE.not().apply("ballooning head"));
        assertFalse(Predicate.ALWAYS_TRUE.not().apply(1812));
        assertTrue(DIV2.not().apply(15));
        assertFalse(DIV5.not().apply(60));
    }

    @Test
    public void testLaziness() throws Exception {
        Predicate<Function1<Integer, Integer>> eqOne = f -> f.apply(1) == 1;
        Predicate<Function1<Integer, Integer>> neqOne = f -> f.apply(1) != 1;
        Predicate<Function1<Integer, Integer>> isNull = f -> f.apply(0) == 1;
        Function1<Integer, Integer> unsafe = a -> 1/a;

        assertTrue(eqOne.or(isNull).apply(unsafe));
        assertFalse(neqOne.and(isNull).apply(unsafe));
    }
}