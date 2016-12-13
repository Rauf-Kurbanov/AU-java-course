package kurbanov.trie;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class SimpleTrie implements Trie, StreamSerializable {

    private int startsWith;
    private int size;
    private boolean isEndOfStr;
    private final Map<Character, SimpleTrie> children;

    public SimpleTrie() {
        children = new HashMap<>();
        isEndOfStr = false;
        startsWith = 0;
        size = 0;
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
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        return howManyStartsWithPrefix(prefix, 0);
    }

    public String trace(int nSpaces) {
        StringBuilder res = new StringBuilder();
        for (Map.Entry<Character, SimpleTrie> entry : children.entrySet()) {
            for (int i = 0; i < nSpaces; i++) {
                res.append("  ");
            }
            res.append(entry.getKey().toString());
            if (entry.getValue().isEndOfStr) {
                res.append("<>");
            } else {
                res.append("  ");
            }
            res.append("\n" + entry.getValue().trace(nSpaces + 2));
        }
        return res.toString();
    }

    @Override
    public void serialize(OutputStream out) throws IOException {
        DataOutputStream dataOStream = new DataOutputStream(out);
        dataOStream.writeBoolean(isEndOfStr);
        dataOStream.writeInt(startsWith);
        dataOStream.writeInt(children.size());

        for (Map.Entry<Character, SimpleTrie> mapEntry : children.entrySet()) {
            dataOStream.writeChar(mapEntry.getKey());
            mapEntry.getValue().serialize(out);
        }
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        DataInputStream dataInStream = new DataInputStream(in);
        isEndOfStr = dataInStream.readBoolean();
        startsWith = dataInStream.readInt();
        children.clear();
        int childrenCount = dataInStream.readInt();

        for (int i = 0; i < childrenCount; i++) {
            char sym = dataInStream.readChar();
            SimpleTrie newTrie = new SimpleTrie();
            newTrie.deserialize(in);
            children.put(sym, newTrie);
        }
    }

    private boolean add(String element, int from) {
        if (contains(element)) {
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
        size++;
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
        size--;
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
