package client;

import client.ex.PartReadException;
import lombok.Getter;
import lombok.ToString;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ToString
public class FileHolder {

    public static final int PART_SIZE = 10_000_000;

//    private final int nParts;

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private int size;
    @Getter
    private boolean isSaved = false;

    private final FileManager fileManager;
    private final byte[][] partsContent;
    private final boolean[] initalized;
    private final int nParts;
    private int nInitalized;

    public FileHolder(int id, String name, int size, final FileManager fileManager) {
        this.id = id;
        this.name = name;
        this.size = size;
        this.isSaved = false;
        this.fileManager = fileManager;

        nParts = (int) Math.ceil(size / PART_SIZE);
        partsContent = new byte[nParts][];
        for (int i = 0; i < size / PART_SIZE; i++) {
            partsContent[i] = new byte[PART_SIZE];
        }
        final int lastSize = size % PART_SIZE;
        if (lastSize > 0) {
            partsContent[nParts - 1] = new byte[lastSize];
        }
        initalized = new boolean[nParts];
    }

    public void readPart(int partId, InputStream in) throws IOException {
        final byte[] content = partsContent[partId];
        final int partSize = partsContent[partId].length;
        int readTotal = 0;
        int readLast = 0;
        while (readTotal < partSize && readLast != -1) {
            readLast = in.read(content, 0, partSize);
            readTotal += readLast;
        }
        if (readTotal < partSize) {
            final String msg = String.join("\n"
                    , String.format("Error while reading part %d of file %s", partId, name)
                    , String.format("Got &d bytes instead of &d", readTotal, PART_SIZE));
            throw new PartReadException(msg);
        }
        initalized[partId] = true;
        nInitalized++;

        if (isComplete()) {
            saveToDisk();
        }
    }

    public List<Integer> getParts() {
        return IntStream.range(0, initalized.length)
                .filter(i -> initalized[i])
                .boxed()
                .collect(Collectors.toList());
    }

    private boolean isComplete() {
        return nInitalized == nParts;
    }

    // TODO allow java to free holder memory
    private void saveToDisk() throws IOException {
        final Path newFile = fileManager.newFile(name);
        final OutputStream os = new FileOutputStream(newFile.toFile());
        for (byte[] aPartsContent : partsContent) os.write(aPartsContent);
    }
}

