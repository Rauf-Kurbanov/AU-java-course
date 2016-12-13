package client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static client.FileStatus.NOT_READY;
import static client.FileStatus.READY;
import static com.google.common.base.Preconditions.checkArgument;

@RequiredArgsConstructor
public class FileManager implements Serializable {

    public final String root;

    @Getter
    private final Map<Integer, FileHolder> files = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, FileStatus> statusMap = new ConcurrentHashMap<>();

    private final Map<Integer, File> serverFileIdToPath = new ConcurrentHashMap<>();

    public void addToIndex(int fileId, String fileName) throws IOException {
        File file = newFile(fileName);
        final FileHolder fileHolder = FileHolder.seededHolder(fileId, fileName, file);
        files.put(fileId, fileHolder);
        serverFileIdToPath.put(fileId, file);
    }

    public Set<Integer> allIds() {
        return serverFileIdToPath.keySet();
    }

    public FileHolder getFileHolder(int fileId) {
        return files.get(fileId);
    }

    public List<Path> allFiles() throws IOException {
        final Path rootPath = Paths.get(root);
        Stream<Path> paths = Files.walk(rootPath);
        return paths.filter(Files::isRegularFile).collect(Collectors.toList());
    }

    private Path stringToPath(String fileName) {
        return Paths.get(root.toString(), fileName);
    }

    public boolean contains(String fileName) throws IOException {
        return allFiles().contains(stringToPath(fileName));
    }

    public long getFileSize(String fileName) {
        Path filePath = stringToPath(fileName);
        return filePath.toFile().length();
    }

    public File newFile(String fileName) throws IOException {
        final File newFile = Paths.get(root, fileName).toFile();
        if (!newFile.exists()) {
            newFile.createNewFile();
        }
        return newFile;
    }

    private void setStatus(String fileName, FileStatus fileStatus) {
        statusMap.put(fileName, fileStatus);
    }

    public void setReady(String fileName) {
        setStatus(fileName, READY);
    }

    public boolean isDownloading(int fileId) {
        return files.containsKey(fileId) &&
                statusMap.getOrDefault(files.get(fileId).getName(), NOT_READY) == NOT_READY;
    }

    public void startDownloading(FileDescr fileDescr) throws IOException {
        final String fileName = fileDescr.getName();
        final FileHolder fileHolder = FileHolder.leechedHolder(fileDescr.getId()
                , fileName
                , fileDescr.getSize()
                , newFile(fileName));
        files.put(fileDescr.getId(), fileHolder);
        statusMap.put(fileDescr.getName(), NOT_READY);
    }

    public FileHolder getDownloadingFile(int fileId) {
        return files.get(fileId);
    }



    public FileStatus getStatus(String fileName) {
        return statusMap.getOrDefault(fileName, NOT_READY);
    }
}

