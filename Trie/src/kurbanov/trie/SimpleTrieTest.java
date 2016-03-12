package kurbanov.trie;

import static org.junit.Assert.*;

public class SimpleTrieTest {

    private SimpleTrie instance;

    @org.junit.Before
    public void setUp() throws Exception {
        instance = new SimpleTrie();
        instance.add("he");
        instance.add("she");
        instance.add("his");
        instance.add("hers");
    }

    @org.junit.Test
    public void testAdd() throws Exception {
        instance.add("shena");
        assertEquals("  s  \n" +
                        "      h  \n" +
                        "          e<>\n" +
                        "              n  \n" +
                        "                  a<>\n" +
                        "  h  \n" +
                        "      e<>\n" +
                        "          r  \n" +
                        "              s<>\n" +
                        "      i  \n" +
                        "          s<>\n"
                , instance.trace(1));
    }

    @org.junit.Test
    public void testAddFlag() throws Exception {
        assertFalse(instance.add("his"));
        assertFalse(instance.add("she"));
        assertTrue(instance.add("me"));
        assertFalse(instance.add("me"));
        assertTrue(instance.add("asssamassa"));
        assertTrue(instance.add("her"));
        assertTrue(instance.add("asssa"));
    }

    @org.junit.Test
    public void testContains() throws Exception {
        assertTrue(instance.contains("he"));
        assertTrue(instance.contains("she"));
        assertTrue(instance.contains("his"));
        assertTrue(instance.contains("hers"));

        assertFalse(instance.contains("her"));
        assertFalse(instance.contains("heshe"));
        assertFalse(instance.contains("h"));
        assertFalse(instance.contains("smth"));
    }

    @org.junit.Test
    public void testRemove() throws Exception {
        instance.remove("he");
        assertEquals("  s  \n" +
                        "      h  \n" +
                        "          e<>\n" +
                        "  h  \n" +
                        "      e  \n" +
                        "          r  \n" +
                        "              s<>\n" +
                        "      i  \n" +
                        "          s<>\n"
                , instance.trace(1));
        instance.add("shenanigans");
        instance.remove("shenanigans");
        assertEquals("  s  \n" +
                        "      h  \n" +
                        "          e<>\n" +
                        "  h  \n" +
                        "      e  \n" +
                        "          r  \n" +
                        "              s<>\n" +
                        "      i  \n" +
                        "          s<>\n"
                , instance.trace(1));
    }

    @org.junit.Test
    public void testRemoveNothing() throws Exception {
        instance.remove("nothing");
        assertEquals("  s  \n" +
                        "      h  \n" +
                        "          e<>\n" +
                        "  h  \n" +
                        "      e<>\n" +
                        "          r  \n" +
                        "              s<>\n" +
                        "      i  \n" +
                        "          s<>\n"
                , instance.trace(1));
    }

    @org.junit.Test
    public void testRemoveFlag() throws Exception {
        assertFalse(instance.remove("notinhere"));
        assertFalse(instance.remove("her"));
        assertTrue(instance.remove("he"));
        assertTrue(instance.remove("hers"));
    }

    @org.junit.Test
    public void testSize() throws Exception {
        assertEquals(4, instance.size());
        instance.remove("he");
        assertEquals(3, instance.size());
        instance.add("same");
        instance.add("same");
        instance.add("same");
        assertEquals(4, instance.size());
        instance.remove("hers");
        instance.remove("hers");
        assertEquals(3, instance.size());
    }

    @org.junit.Test
    public void testHowManyStartsWithPrefix() throws Exception {
        assertEquals(2, instance.howManyStartsWithPrefix("he"));
        assertEquals(3, instance.howManyStartsWithPrefix("h"));
        assertEquals(1, instance.howManyStartsWithPrefix("sh"));
        assertEquals(0, instance.howManyStartsWithPrefix("hero"));
        assertEquals(4, instance.howManyStartsWithPrefix(""));
    }

    @org.junit.Test
    public void testTrace() throws Exception {
        assertEquals("  s  \n" +
                        "      h  \n" +
                        "          e<>\n" +
                        "  h  \n" +
                        "      e<>\n" +
                        "          r  \n" +
                        "              s<>\n" +
                        "      i  \n" +
                        "          s<>\n"
                , instance.trace(1));
    }
}