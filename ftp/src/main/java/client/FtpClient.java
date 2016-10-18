package client;

import protocol.FtpProtocol;
import protocol.Protocol;
import protocol.FtpFile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class FtpClient implements Client {

    private boolean connected = false;
    private Socket clientSocket;
    private DataOutputStream netOut;
    private DataInputStream netIn;
    final Protocol protocol = new FtpProtocol();

    @Override
    public void connect(String hostName, int port) throws IOException {
        if (connected)
            return;

        clientSocket = new Socket(hostName, port);
        netOut = new DataOutputStream(clientSocket.getOutputStream());
        netIn = new DataInputStream(clientSocket.getInputStream());

        connected = true;
    }

    @Override
    public void disconnect() throws IOException {
        if (!connected)
            return;

        netIn.close();
        netOut.close();
        clientSocket.close();
    }

    @Override
    public List<FtpFile> executeList(String path) throws IOException {
        if (!connected)
            return null;
        protocol.answerList(path, netOut);
        return protocol.readListResponse(netIn);
    }

    @Override
    public void executeGet(String path, OutputStream out) throws IOException {
        if (!connected)
            return;
        protocol.answerGet(path, netOut);
        protocol.readGetResponse(netIn, out);
    }
}
