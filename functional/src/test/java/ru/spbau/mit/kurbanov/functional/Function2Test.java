package ru.spbau.mit.kurbanov.functional;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Function2Test {

    private final Function2<Double, Double, Double> sum = (a, b) -> a + b;
    private final Function2<Double, Double, Double> mul = (a, b) -> a * b;
    private final Function1<Double, Integer> square = value -> (int) (value * value);
    private final Function2<Long, Long, Long> div = (a, b) -> a / b;
    private final Function2<Integer, Integer, Integer> mod = (a, b) -> a % b;

    @Test
    public void testCompose() throws Exception {
        assertEquals(25, sum.compose(square).apply(2.0, 3.0).intValue());
        assertEquals(0, mul.compose(square).apply(0.0, -234442.).intValue());
    }

    @Test
    public void testBind1() throws Exception {
        Function1<Double, Double> mul2 = mul.bind1(2.);
        Function1<Integer, Integer> mod237 = mod.bind1(237);

        assertEquals(244, mul2.apply(122.).intValue());
        assertEquals(0, mul2.apply(0.).intValue());
        assertEquals(2, mod237.apply(5).intValue());
    }

    @Test
    public void testBind2() throws Exception {
        Function2<Integer, Integer, Integer> div = (a, b) -> a / b;
        Function1<Integer, Integer> div2 = div.bind2(2);

        assertEquals(1, div2.apply(2).intValue());
        assertEquals(2, div2.apply(4).intValue());
        assertEquals(2, div2.apply(5).intValue());
    }

    @Test
    public void testCurry() throws Exception {
        assertEquals(5, div.curry().apply(10L).apply(2L).intValue());
        assertEquals(10, div.curry().apply(10L).apply(1L).intValue());
    }
}