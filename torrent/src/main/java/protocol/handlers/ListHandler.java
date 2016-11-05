package protocol.handlers;

import server.FileInfo;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ListHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
        Collection<FileInfo> files = state.getFilesById().values();
        out.writeInt(files.size());
        for (FileInfo fi : files) {
            out.writeUTF(fi.getName());
            out.writeLong(fi.getSize());
        }
    }
}
