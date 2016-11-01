package client;

import protocol.FtpFile;
import protocol.FtpProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class FtpClient {

    private Socket clientSocket;
    private DataOutputStream netOut;
    private DataInputStream netIn;
    private final FtpProtocol protocol = new FtpProtocol();

    public FtpClient(String hostName, int port) throws IOException {
        clientSocket = new Socket(hostName, port);
        netOut = new DataOutputStream(clientSocket.getOutputStream());
        netIn = new DataInputStream(clientSocket.getInputStream());
    }

    public void disconnect() throws IOException {
        netOut.writeInt(-1);
        netOut.flush();
        clientSocket.close();
    }

    public List<FtpFile> executeList(String path) throws IOException {
        protocol.sendListRequest(path, netOut);
        return protocol.readListResponse(netIn);
    }

    public void executeGet(String path, OutputStream out) throws IOException {
        protocol.sendGetRequest(path, netOut);
        protocol.readGetResponse(netIn, out);
    }
}
