package ru.spbau.mit.kurbanov.functional;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {

    private final Predicate<Integer> div2 = x -> x % 2 ==0;
    private final Predicate<Integer> div5 = x -> x % 5 ==0;

    @Test
    public void testOr() throws Exception {
        assertTrue(div2.or(div5).apply(5));
        assertTrue(div2.or(div5).apply(4));
        assertTrue(div2.or(div5).apply(6));
        assertFalse(div2.or(div5).apply(17));
    }

    @Test
    public void testAnd() throws Exception {
        assertTrue(div2.and(div5).apply(10000000));
        assertFalse(div2.and(div5).apply(444));
        assertFalse(div2.and(div5).apply(555));
        assertFalse(div2.and(div5).apply(222));
    }

    @Test
    public void testNot() throws Exception {
        assertTrue(Predicate.ALWAYS_FALSE.not().apply("ballooning head"));
        assertFalse(Predicate.ALWAYS_TRUE.not().apply(1812));
        assertTrue(div2.not().apply(15));
        assertFalse(div5.not().apply(60));
    }
}