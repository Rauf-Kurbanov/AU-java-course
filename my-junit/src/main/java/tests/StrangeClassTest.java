package tests;

import my_junit.Test;
import strange.StrangeClass;

import static my_junit.MyAsserts.assertEquals;

public class StrangeClassTest {

    private final StrangeClass instance = new StrangeClass();

//    @Test
//    public void testToString() {
//        assertEquals("someString", "someString");
//    }
//
//    @Test(expected = StrangeException.class)
//    public void exceptionTest() throws StrangeException {
//        instance.throwStrange();
//    }
//
//    @Test(ignore = "too lazy to test")
//    public void ignored() {
//        assertEquals("obviously", "dont");
//    }

    @Test
    public void failMe() {
        assertEquals("a", "b");
    }
}
