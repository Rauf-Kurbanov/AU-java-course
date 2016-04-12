package ru.spbau.mit.kurbanov.music;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() throws FileNotFoundException {
        ArrayList<String> paths = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();

        URL path = classLoader.getResource("text");
        if (path == null)
            throw new FileNotFoundException();

        paths.add(path.getPath());
        List<String> result = SecondPartTasks.findQuotes(paths, "liar");
        List<String> expected = Arrays.asList("You’ve got to dance little liar",
                "The liar take a lot less time", "The liar takes a lot less");

        assertEquals(expected, result);
    }

    @Test
    public void testPiDividedBy4() {
        assertEquals(Math.PI / 4.0, SecondPartTasks.piDividedBy4(), 0.001);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> authorsMap = new HashMap<>();
        authorsMap.put("TLSP", Arrays.asList("Dracula Teeth", "Everything You've Come To Expect", "Miracle Aligner"));
        authorsMap.put("Morphine", Arrays.asList("Buena", "Rope on Fire"));
        authorsMap.put("U2", Collections.emptyList());
        assertEquals("TLSP", SecondPartTasks.findPrinter(authorsMap));
    }

    @Test
    public void testCalculateGlobalOrder() {
        final Map<String, Integer> map1 = new HashMap<>();
        final Map<String, Integer> map2 = new HashMap<>();

        map1.put("A", 1);
        map1.put("B", 2);
        map1.put("C", 3);

        map2.put("A", 3);
        map2.put("C", 3);

        final List<Map<String, Integer>> mapList = new ArrayList<>();
        mapList.add(map1);
        mapList.add(map2);

        final Map<String, Integer> globalOrder = SecondPartTasks.calculateGlobalOrder(mapList);
        assertEquals(4, (int)globalOrder.get("A"));
        assertEquals(2, (int)globalOrder.get("B"));
        assertEquals(6, (int)globalOrder.get("C"));
    }
}