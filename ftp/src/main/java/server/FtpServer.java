package server;

import protocol.FtpProtocol;
import protocol.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FtpServer implements Server {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private void runServer(int portNumber) {
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        ) {
            Protocol protocol = new FtpProtocol();
            while (!Thread.interrupted()) {
                protocol.answerQuery(in, out);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void start(int portNumber) {
        executor.execute(() -> runServer(portNumber));
    }

    public void stop() {
        executor.shutdownNow();
    }
}
