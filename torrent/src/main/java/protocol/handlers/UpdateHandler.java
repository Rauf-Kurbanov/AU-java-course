package protocol.handlers;

import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UpdateHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
        short clientPort = in.readShort();
        int count = in.readInt();
        while (count-- > 0) {
            int fileId = in.readInt();
            Socket clientSocket = new Socket(clientPort);
            boolean status = state.update(clientPort, fileId);
            out.writeBoolean(status);
        }
    }
}
