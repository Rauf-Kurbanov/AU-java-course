package client;

import client.ex.PartReadException;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
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

    private final File file;
    private final boolean[] initalized;
    @Getter
    private final int nParts;
    @Getter
    private int acquiredParts = 0;
    
    private FileHolder(int id, String name, int size, File file) throws IOException {
        this.id = id;
        this.name = name;
        this.size = size;
        this.file = file;

        // http://stackoverflow.com/questions/7139382/java-rounding-up-to-an-int-using-math-ceil
        nParts = (size + PART_SIZE - 1) / PART_SIZE;
        initalized = new boolean[nParts];
    }

    public static FileHolder seededHolder(int id, String name, final File file) throws IOException {
        final FileHolder fileHolder = new FileHolder(id, name, (int) file.length(), file);
        Arrays.fill(fileHolder.initalized, true);
        return fileHolder;
    }

    public static FileHolder leechedHolder(int id, String name, int size, final File file) throws IOException {
        return new FileHolder(id, name, size, file);
    }

    private int partSize(int partId) {
        return size / PART_SIZE <= partId ? size % PART_SIZE : PART_SIZE;
    }

    public byte[] getPart(int partId) throws IOException {
        if (!initalized[partId]) {
            return null;
        }

        final byte[] content = new byte[PART_SIZE];
        int readTotal = 0;
        int readLast = 0;
        final int partSize = partSize(partId);

        try (final RandomAccessFile raFile = new RandomAccessFile(file, "r")) {
            raFile.seek(partId * PART_SIZE);

            while (readTotal < partSize && readLast != -1) {
                readLast = raFile.read(content, readTotal, partSize - readTotal);
                readTotal += Math.max(0, readLast);
            }
        }

        if (readTotal < partSize) {
            final String msg = String.join("\n"
                    , String.format("Error while reading part %d of file %s", partId, name)
                    , String.format("Got &d bytes instead of &d", readTotal, PART_SIZE));
            throw new PartReadException(msg);
        }
        return content;
    }

    public FileStatus readPart(int partId, InputStream in) throws IOException {
        log.info("Calling readPart");
        final byte[] content = new byte[PART_SIZE];
        int readTotal = 0;
        int readLast = 0;
        while (readTotal < PART_SIZE && readLast != -1) {
            readLast = in.read(content, readTotal, PART_SIZE - readTotal);
            readTotal += Math.max(0, readLast);
        }

        if (readTotal < PART_SIZE) {
            final String msg = String.join("\n"
                    , String.format("Error while reading part %d of file %s", partId, name)
                    , String.format("Got &d bytes instead of &d", readTotal, PART_SIZE));
            throw new PartReadException(msg);
        }

        try (final RandomAccessFile raFile = new RandomAccessFile(file, "rw")) {
            raFile.seek(partId * PART_SIZE);
            raFile.write(content, 0, partSize(partId));
        }

        initalized[partId] = true;
        acquiredParts++;

        boolean isReady = true;
        for (boolean b : initalized) {
            if (!b) {
                isReady = false;
            }
        }
        return isReady ? FileStatus.READY : FileStatus.NOT_READY;
    }

    public List<Integer> getParts() {
        return IntStream.range(0, initalized.length)
                .filter(i -> initalized[i])
                .boxed()
                .collect(Collectors.toList());
    }
}

