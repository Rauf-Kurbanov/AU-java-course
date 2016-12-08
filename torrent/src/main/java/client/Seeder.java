package client;

import lombok.EqualsAndHashCode;
import protocol.Protocol;

import java.io.File;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;

//@RequiredArgsConstructor
// why?
@EqualsAndHashCode
// make it implement server
public class Seeder {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private ServerSocket serverSocket = createServerSocket();
    // TODO maybr not an argument
    private final Protocol protocol;
    private final FileManager fileManager;

    private final Map<Integer, FileHolder> seededFiles = new ConcurrentHashMap<>();
    private final Map<Integer, Path> serverFileIdToPath = new ConcurrentHashMap<>();

    private ServerSocket createServerSocket() throws IOException {

        int n_tries = 50;
        final Random random = new Random();

        ServerSocket toReturn = null;
        while (n_tries-- > 0) {
            int port = random.nextInt(Short.MAX_VALUE);
            try {
                toReturn = new ServerSocket(port);
                System.out.printf("Generated port: %d\n", port);
                break;
            } catch (IllegalArgumentException | BindException e) {
                continue;
            }
        }
        if (n_tries == 0) {
            throw new IOException("no free port found");
        }
        return toReturn;
    }

    public Seeder(Protocol protocol, final FileManager fileManager) throws IOException {
        this.protocol = protocol;
        this.fileManager = fileManager;
    }

    public short getPort() {
        return (short) serverSocket.getLocalPort();
    }

    private void runServer() throws IOException {
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.execute(() -> protocol.answerClientQuery(clientSocket, this));
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
            }
        }
    }

//    @Override
    public void start() {
        serverThreadExecutor.execute(() -> {
            try {
                runServer();
            } catch (IOException e) {
                throw new RuntimeException("Can't start server", e);
            }
        });
    }

//    @Override
    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        executor.shutdownNow();
        serverThreadExecutor.shutdownNow();
    }

    public long getFileSizeToUpload(String fileName) throws IOException {
        checkArgument(fileManager.contains(fileName),
                String.format("file %s doesn't exist in %s", fileName, fileManager.root.getFileName()));
        return fileManager.getFileSize(fileName);
    }

    public void addToIndex(int fileId, String fileName) throws IOException {
        File file = fileManager.newFile(fileName);
//        final FileHolder fh = new FileHolder(fileId, fileName, (int) file.length(), file, true);
//        final FileHolder fh = new FileHolder(fileId, fileName, file, true);
        final FileHolder fileHolder = FileHolder.seededHolder(fileId, fileName, file);
        seededFiles.put(fileId, fileHolder);
        serverFileIdToPath.put(fileId, file.toPath());
    }

    public Set<Integer> allIds() {
        return serverFileIdToPath.keySet();
    }

    public FileHolder getSeededFile(int fileId) {
        return seededFiles.get(fileId);
    }

}
