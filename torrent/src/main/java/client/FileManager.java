package client;

import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileManager {

    public final Path root;
    private final Map<Integer, Path> serverFileIdToPath = new ConcurrentHashMap<>();
    private final Map<Integer, FileHolder> downloadingFiles = new ConcurrentHashMap<>();
    private final Map<Integer, FileHolder> seededFiles = new ConcurrentHashMap<>();

    public void addToIndex(int fileId, String fileName) throws IOException {
        final Path path = stringToPath(fileName);
        final FileHolder fh = new FileHolder(fileId, fileName, (int) path.toFile().length(), this, true);
        seededFiles.put(fileId, fh);
        serverFileIdToPath.put(fileId, path);
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

    public FileHolder getDownloadingFile(int fileId) {
        return downloadingFiles.get(fileId);
    }

    public FileHolder getSeededFile(int fileId) {
        return seededFiles.get(fileId);
    }

    public void startDownloading(FileDescr fileDescr) throws IOException {
        final FileHolder fileHolder = new FileHolder(fileDescr.getId()
                , fileDescr.getName()
                , fileDescr.getSize()
                , this
                , false);
        downloadingFiles.put(fileDescr.getId(), fileHolder);
    }

    public File newFile(String fileName) throws IOException {
        return Paths.get(root.toString(), fileName).toFile();
    }
}
