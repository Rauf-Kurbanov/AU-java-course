package server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import protocol.Protocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RequiredArgsConstructor
public class ThreadPooledServer implements Server {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Protocol protocol;
    private ServerSocket serverSocket;
    private final ServerState state = new ServerState();

//    private void printState() {
//        System.out.println(state.toString());
//    }

    private synchronized void runServer(int portNumber) throws IOException {
        log.info("Trying to accept socket");
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                log.info("Entering serverSocket.accept()");
                Socket clientSocket = serverSocket.accept();
                log.info("Exiting serverSocket.accept()");

                synchronized (state) {
                    executor.execute(() -> protocol.answerServerQuery(clientSocket, state));
                }
//                printState();
                log.info("Passed query processing");
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
            }
        }
        log.info("server main thread stops running");
    }

    @Override
    public void start(int portNumber) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        log.info("stopping server");
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
