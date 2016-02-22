package kurbanov.trie;

import java.util.HashMap;
import java.util.Map;

public class SimpleTrie implements Trie {

    public SimpleTrie() {
        children = new HashMap<>();
        isEndOfStr = false;
        startsWith = 0;
        size = 0;
    }

    @Override
    public boolean add(String element) {
        if (contains(element))
            return false;
        if (element.isEmpty()) {
            if (!isEndOfStr) {
                isEndOfStr = true;
                startsWith++;
            }
        } else {
            char letter = element.charAt(0);
            String tail = element.substring(1);
            SimpleTrie newNode = children.getOrDefault(letter, new SimpleTrie());
            if (newNode.add(tail))
                startsWith++;
            children.put(letter, newNode);
        }
        size++;
        return true;
    }

    @Override
    public boolean contains(String element) {
        if (element.isEmpty()) {
            return isEndOfStr;
        }
        char letter = element.charAt(0);
        String tail = element.substring(1);
        return children.containsKey(letter) && children.get(letter).contains(tail);
    }

    @Override
    public boolean remove(String element) {
        if (!contains(element))
            return false;

        if (element.isEmpty()) {
            isEndOfStr = false;
        } else {
            char letter = element.charAt(0);
            String tail = element.substring(1);
            SimpleTrie toDel = children.get(letter);
            if (toDel.startsWith == 1) {
                children.remove(letter);
            } else {
                toDel.remove(tail);
            }
        }
        startsWith--;
        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        if (prefix.isEmpty()) {
            return startsWith;
        }
        char letter = prefix.charAt(0);
        String tail = prefix.substring(1);
        if (children.containsKey(letter)) {
            return children.get(letter).howManyStartsWithPrefix(tail);
        }
        return 0;
    }

    public String trace(int nSpaces) {
        String res = "";
        for (Map.Entry<Character, SimpleTrie> entry : children.entrySet())
        {
            for (int i = 0; i < nSpaces; i++) {
                res += "  ";
            }
            res += entry.getKey().toString();
            if (entry.getValue().isEndOfStr) {
                res += "<>";
            } else {
                res += "  ";
            }
            res += "\n" + entry.getValue().trace(nSpaces + 2);
        }
        return res;
    }

    private int startsWith;
    private int size;
    private boolean isEndOfStr;
    private Map<Character, SimpleTrie> children;
}
