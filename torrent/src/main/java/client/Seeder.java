package client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import protocol.Protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

@RequiredArgsConstructor
@EqualsAndHashCode
public class Seeder implements Runnable {

    private ServerSocket serverSocket;
    private final Protocol protocol;
    @Getter
    private final InetAddress ip;
    @Getter
    private final int port;
//  TODO fill
    private ClientState clientState = null;

    private void runServer(int portNumber) throws IOException {
        serverSocket = new ServerSocket(portNumber);
        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();

                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                new Thread(() -> protocol.answeClientQuery(clientSocket, clientState));
            } catch (IOException e) {
                System.out.println("Cannot open client socket");
            }
        }
    }

    @Override
    public void run() {
        new Thread(() -> {
            try {
                runServer(port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}
