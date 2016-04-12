package kurbanov.trie;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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
                , instance.trace());
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
                , instance.trace());
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
                , instance.trace());
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
                , instance.trace());
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
                , instance.trace());
    }

    @org.junit.Test
    public void failed() {
        final SimpleTrie trie = new SimpleTrie();

        assertTrue(trie.add("aaa"));
        assertTrue(trie.add("aa"));
        assertEquals(2, trie.howManyStartsWithPrefix(""));
    }


    @org.junit.Test
    public void testSimple() {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.contains("abc"));
        assertEquals(1, trie.size());
        assertEquals(1, trie.howManyStartsWithPrefix("abc"));
    }

    @org.junit.Test
    public void testSimpleSerialization() throws IOException {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.add("cde"));
        assertEquals(2, trie.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) trie).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Trie newTrie = instance();
        ((StreamSerializable) newTrie).deserialize(inputStream);

        assertTrue(newTrie.contains("abc"));
        assertTrue(newTrie.contains("cde"));
        assertEquals(2, trie.size());
    }

    @org.junit.Test(expected=IOException.class)
    public void testSimpleSerializationFails() throws IOException {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.add("cde"));

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Fail");
            }
        };

        ((StreamSerializable) trie).serialize(outputStream);
    }

    public static Trie instance() {
        try {
            return (Trie) Class.forName("kurbanov.trie.SimpleTrie").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}