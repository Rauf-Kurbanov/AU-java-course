package protocol.handlers;

import client.ClientState;
import server.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientStatHandler implements ClientRequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out) throws IOException {
//        int fileId = in.readInt();
//        Collection<Integer> parts = null;
//        out.writeInt(parts.getFileSize());
//        for (int p : parts) {
//            out.writeInt(p);
//        }
//    }

    @Override
    public void handle(Socket clientSocket, ClientState state) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        int fileId = in.readInt();
        FileInfo fileInfo = state.getFileInfo(fileId);
        out.writeInt(fileInfo.getParts().size());
        for (int p : fileInfo.getParts()) {
            out.writeInt(p);
        }
    }
}
