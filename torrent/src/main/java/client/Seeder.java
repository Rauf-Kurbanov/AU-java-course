package client;

import lombok.EqualsAndHashCode;
import protocol.Protocol;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//@RequiredArgsConstructor
// why?
@EqualsAndHashCode
// make it implement server
public class Seeder {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    private ServerSocket serverSocket = createServerSocket();
    private final Protocol protocol;
    private final FileManager fileManager;

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
                // TODO reuse threadpooled server
                executor.execute(() -> protocol.answerClientQuery(clientSocket, fileManager));
//                new Thread(() -> protocol.answerClientQuery(clientSocket, fileManager));
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
//        printState();
    }
}
