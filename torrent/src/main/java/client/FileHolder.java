package client;

import client.ex.PartReadException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@ToString
public class FileHolder {

    public static final int PART_SIZE = 10_000_000;

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

        nParts = (int) Math.ceil((double) size / PART_SIZE);
        partsContent = new byte[nParts][];
        for (int i = 0; i < size / PART_SIZE; i++) {
            partsContent[i] = new byte[PART_SIZE];
        }
        final int lastSize = size % PART_SIZE;
        if (lastSize > 0) {
            partsContent[nParts - 1] = new byte[PART_SIZE];
        }
        initalized = new boolean[nParts];
    }

    private void readPartFromFile(int partId, InputStream fis) throws IOException {
        int partSize = PART_SIZE;
        if (partId == nParts - 1 && size % PART_SIZE > 0) {
            partSize = size % PART_SIZE;
        }
        final byte[] content = partsContent[partId];
        int readTotal = 0;
        int readLast = 0;
        while (readTotal < partSize && readLast != -1) {
            readLast = fis.read(content, readTotal, partSize - readTotal);
            readTotal += Math.max(0, readLast);
        }
        if (readTotal < partSize) {
            final String msg = String.join("\n"
                    , String.format("Error while reading part %d of file %s", partId, name)
                    , String.format("Got &d bytes instead of &d", readTotal, PART_SIZE));
            throw new PartReadException(msg);
        }

        initalized[partId] = true;
        nInitalized++;
    }

    public FileHolder(int id, String name, int size, final FileManager fileManager, Path path) throws IOException {
        this(id, name, size, fileManager);
        final InputStream fis = new FileInputStream(path.toFile());
        for (int i = 0; i < nParts; i++) {
            readPartFromFile(i, fis);
        }
    }

    public byte[] getPart(int partId) {
        if (!initalized[partId]) {
            return null;
        }
        return partsContent[partId];
    }

    // TODO test carefully
    public void readPart(int partId, InputStream in) throws IOException {
        log.info("Calling readPart");
        final byte[] content = partsContent[partId];
        final int partSize = PART_SIZE;
        int readTotal = 0;
        int readLast = 0;
        while (readTotal < partSize && readLast != -1) {
            readLast = in.read(content, readTotal, partSize - readTotal);
            readTotal += Math.max(0, readLast);
            System.out.println("AAAAAAaaAAAAAA");
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
        log.info(String.format("Saving to disk name: %s, size: %d\n", name, size));
        final Path newFile = fileManager.newFile(name);
        final OutputStream os = new FileOutputStream(newFile.toFile());
        final int lastSize = size % PART_SIZE;
        for (int i = 0; i < partsContent.length - 1; i++) {
            os.write(partsContent[i]);
        }
        if (lastSize > 0) {
            os.write(partsContent[nParts - 1], 0, lastSize);
        } else {
            os.write(partsContent[nParts - 1]);
        }
    }
}

