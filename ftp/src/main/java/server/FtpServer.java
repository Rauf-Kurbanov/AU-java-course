package server;

import lombok.extern.slf4j.Slf4j;
import protocol.FtpProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class FtpServer {

    private final ExecutorService serverThreadExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private ServerSocket serverSocket;

    private void runServer(int portNumber, FtpProtocol protocol) throws IOException {
        log.info("Trying to accept socket");
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                log.info("Entering serverSocket.accept()");
                Socket clientSocket = serverSocket.accept();
                log.info("Exiting serverSocket.accept()");

                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                executor.execute(() -> protocol.answerQuery(in, out));
                log.info("Passed query processing");
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
            }
        }
    }


    public void start(int portNumber, FtpProtocol protocol) {
        serverThreadExecutor.execute(() -> {
            try {
                runServer(portNumber, protocol);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void stop() {
        log.info("stopping server");
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        executor.shutdown();
    }
}
