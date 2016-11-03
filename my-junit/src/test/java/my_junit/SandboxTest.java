package my_junit;

import org.junit.*;

import static org.junit.Assert.assertEquals;

public class SandboxTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void toPrint() throws Exception {
        assertEquals(true, true);
    }

//    @Ignore
    @org.junit.Test()
    public void ignored() {
        assertEquals(true, false);
    }

}