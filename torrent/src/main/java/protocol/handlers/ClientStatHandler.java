package protocol.handlers;

import client.ClientState;
import client.FileHolder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientStatHandler implements ClientRequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out,
                       Socket clientSocket, ClientState state) throws IOException {
        final int fileId = in.readInt();

        final FileHolder fileHolder = state.getFileInfo(fileId);
        out.writeInt(fileHolder.getParts().size());
        for (int p : fileHolder.getParts()) {
            out.writeInt(p);
        }
    }
}
