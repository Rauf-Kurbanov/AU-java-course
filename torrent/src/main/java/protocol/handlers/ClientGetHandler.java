package protocol.handlers;

import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ClientGetHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
        int fileId = in.readInt();
        int part = in.readInt();
        byte[] content = null;
        out.write(content);
    }
}
