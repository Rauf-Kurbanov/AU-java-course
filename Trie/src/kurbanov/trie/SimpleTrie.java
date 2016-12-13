package kurbanov.trie;

import java.util.HashMap;
import java.util.Map;

public class SimpleTrie implements Trie {

    private int startsWith;
    private boolean isEndOfStr;
    private final Map<Character, SimpleTrie> children;

    public SimpleTrie() {
        children = new HashMap<>();
        isEndOfStr = false;
        startsWith = 0;
    }

    @Override
    public boolean add(String element) {
        return add(element, 0);
    }

    @Override
    public boolean contains(String element) {
        return contains(element, 0);
    }

    @Override
    public boolean remove(String element) {
        return remove(element, 0);
    }

    @Override
    public int size() {
        return startsWith;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return howManyStartsWithPrefix(prefix, 0);
    }

    public String trace() {
        return trace(1);
    }

    private String trace(int nSpaces) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<Character, SimpleTrie> entry : children.entrySet()) {
            for (int i = 0; i < nSpaces; i++) {
                res.append("  ");
            }
            res.append(entry.getKey());
            if (entry.getValue().isEndOfStr) {
                res.append("<>");
            } else {
                res.append("  ");
            }
            res.append("\n");
            res.append(entry.getValue().trace(nSpaces + 2));
        }
        return res.toString();
    }

    private boolean add(String element, int from) {
        if (contains(element, from)) {
            return false;
        }
        if (from >= element.length()) {
            isEndOfStr = true;
            startsWith++;
        } else {
            char letter = element.charAt(from);
            SimpleTrie newNode = children.getOrDefault(letter, new SimpleTrie());
            if (newNode.add(element, from + 1)) {
                startsWith++;
            }
            children.put(letter, newNode);
        }

        return true;
    }

    private boolean contains(String element, int from) {
        if (from >= element.length()) {
            return isEndOfStr;
        }
        char letter = element.charAt(from);
        return children.containsKey(letter) && children.get(letter).contains(element, from + 1);
    }

    private boolean remove(String element, int from) {
        if (!contains(element, from)) {
            return false;
        }
        if (from >= element.length()) {
            isEndOfStr = false;
        } else {
            char letter = element.charAt(from);
            SimpleTrie toDel = children.get(letter);
            if (toDel.startsWith == 1) {
                children.remove(letter);
            } else {
                toDel.remove(element, from + 1);
            }
        }
        startsWith--;
        return true;
    }

    private int howManyStartsWithPrefix(String prefix, int from) {
        if (from >= prefix.length()) {
            return startsWith;
        }
        char letter = prefix.charAt(from);
        if (children.containsKey(letter)) {
            return children.get(letter).howManyStartsWithPrefix(prefix, from + 1);
        }
        return 0;
    }
}
