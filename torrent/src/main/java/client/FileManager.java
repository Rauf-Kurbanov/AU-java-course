package client;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileManager {

    public final Path root;
    private final Map<Integer, Path> serverFileIdToPath = new HashMap<>();
    private final Map<Integer, FileHolder> downloadingFiles = new ConcurrentHashMap<>();
    private final Map<Integer, FileHolder> seededFiles = new ConcurrentHashMap<>();

    public void addToIndex(int fileId, String fileName) throws IOException {
        final Path path = stringToPath(fileName);
        serverFileIdToPath.put(fileId, path);
        seededFiles.put(fileId, new FileHolder(fileId, fileName, (int) path.toFile().length(), this, path));
    }

    public Set<Integer> allIds() {
        return serverFileIdToPath.keySet();
    }

    public List<Path> allFiles() throws IOException {
        Stream<Path> paths = Files.walk(root);
        return paths.filter(Files::isRegularFile).collect(Collectors.toList());
    }

    private Path stringToPath(String fileName) {
        return Paths.get(root.toString(), fileName);
    }

    public boolean contains(String fileName) throws IOException {
        return allFiles().contains(stringToPath(fileName));
    }

    public boolean isDownloading(int fileId) {
        return downloadingFiles.containsKey(fileId);
    }

    public long getFileSize(String fileName) {
        Path filePath = stringToPath(fileName);
        return filePath.toFile().length();
    }

    // // TODO: 26.11.16 rename
    public FileHolder getDownloadingFile(int fileId) {
        return downloadingFiles.get(fileId);
    }

    public FileHolder getSeededFile(int fileId) {
        return seededFiles.get(fileId);
    }

    public void startDownloading(final FileDescr fileDescr) {
        final FileHolder fileHolder = new FileHolder(fileDescr.getId()
                , fileDescr.getName()
                , fileDescr.getSize()
                , this);
        downloadingFiles.put(fileDescr.getId(), fileHolder);
    }

    public Path newFile(String fileName) {
        return Paths.get(root.toString(), fileName);
    }
}
