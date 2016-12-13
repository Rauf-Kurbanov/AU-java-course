package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {

    protected Socket clientSocket;
    protected DataOutputStream netOut;
    protected DataInputStream netIn;

    public void connect(String hostName, int port) throws IOException {
        clientSocket = new Socket(hostName, port);
        netOut = new DataOutputStream(clientSocket.getOutputStream());
        netIn = new DataInputStream(clientSocket.getInputStream());
    }

    public void disconnect() throws IOException {
        netOut.writeInt(-1);
        netOut.flush();
        clientSocket.close();
    }
}
