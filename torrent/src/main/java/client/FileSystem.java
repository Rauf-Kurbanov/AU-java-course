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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class FileSystem {

    public final Path root;
    // TODO maybe arraylist
    private Map<Integer, Path> serverFileIdToPath = new HashMap<>();

    public void addToIndex(int fileId, String fileName) {
        final Path path = stringToPath(fileName);
        serverFileIdToPath.put(fileId, path);
    }

    public Set<Integer> allIds() {
        return serverFileIdToPath.keySet();
    }

    public List<Path> allFiles() throws IOException {
        Stream<Path> paths = Files.walk(root);
            return paths.filter(Files::isRegularFile).collect(Collectors.toList());
//        }
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

//    void add(Path file) {
//        if (!file.startsWith(root)) {
//            throw new InvalidPathException(String.format("path: %s should be subdirrectory of %s",
//                    file, root));
//        }
//    }

    public static void main(String[] args) throws IOException {
        Path some = Paths.get("/home/rauf/Videos");
        FileSystem fs = new FileSystem(some);
    }

    private class InvalidPathException extends RuntimeException {
        public InvalidPathException(String str) {
            super(str);
        }
    }
}
