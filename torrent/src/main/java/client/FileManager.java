package client;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Maybe move id -> string map into seeder and leecher
@RequiredArgsConstructor
public class FileManager {

    public final Path root;

    @Getter
    private final Map<Integer, FileHolder> files = new ConcurrentHashMap<>();
    @Getter
    private final Map<String, FileStatus> status = new ConcurrentHashMap<>();

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

    public long getFileSize(String fileName) {
        Path filePath = stringToPath(fileName);
        return filePath.toFile().length();
    }

    public File newFile(String fileName) throws IOException {
        return Paths.get(root.toString(), fileName).toFile();
    }
}
