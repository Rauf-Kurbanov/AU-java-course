package protocol.handlers;

import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class UploadHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
        String name = in.readUTF();
        long size = in.readLong();
        out.writeInt(state.upload(name, size));

    }
}
