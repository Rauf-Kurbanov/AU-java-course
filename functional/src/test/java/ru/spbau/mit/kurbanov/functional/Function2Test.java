package ru.spbau.mit.kurbanov.functional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Function2Test {

    private static final Function2<Double, Double, Double> SUM = (a, b) -> a + b;
    private static final Function2<Double, Double, Double> MUL = (a, b) -> a * b;
    private static final Function1<Double, Integer> SQUARE = value -> (int) (value * value);
    private static final Function2<Long, Long, Long> DIV = (a, b) -> a / b;
    private static final Function2<Integer, Integer, Integer> MOD = (a, b) -> a % b;

    @Test
    public void testCompose() throws Exception {
        assertEquals(25, SUM.compose(SQUARE).apply(2.0, 3.0).intValue());
        assertEquals(0, MUL.compose(SQUARE).apply(0.0, -234442.).intValue());
    }

    @Test
    public void testBind1() throws Exception {
        Function1<Double, Double> mul2 = MUL.bind1(2.);
        Function1<Integer, Integer> mod237 = MOD.bind1(237);

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
        assertEquals(5, DIV.curry().apply(10L).apply(2L).intValue());
        assertEquals(10, DIV.curry().apply(10L).apply(1L).intValue());
    }

    @Test
    public void testWildcard() throws Exception {
        Function1<List, Integer> listSize = List::size;
        Function2<Integer, Integer, ArrayList> packInList = (a, b) -> {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(a);
            list.add(b);
            return list;
        };
        assertEquals(2, packInList.compose(listSize).apply(2, 3).intValue());
    }
}