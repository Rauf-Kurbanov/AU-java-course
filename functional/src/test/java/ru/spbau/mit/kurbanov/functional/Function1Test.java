package ru.spbau.mit.kurbanov.functional;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class Function1Test {

    @Test
    public void testCompose() throws Exception {
        Function1<Integer, Integer> square = x -> x * x;
        Function1<Integer, Integer> plus1 = x -> x + 1;
        Function1<Integer, Integer> plus2 = x -> x + 2;
        Function1<Integer, Integer> plus3 = x -> x + 3;

        assertEquals(256, (long)square.compose(square).compose(square).apply(2));
        assertEquals(6, (long)plus1.compose(plus2).compose(plus3).apply(0));
    }

    @Test
    public void testWildcard() throws Exception {
        Function1<List, String> firstAsStr = l -> l.iterator().next().toString();
        Function1<Integer, ArrayList> arrayListByLen = x -> {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(x);
            return list;
        };
        assertEquals("42", arrayListByLen.compose(firstAsStr).apply(42));
    }

}