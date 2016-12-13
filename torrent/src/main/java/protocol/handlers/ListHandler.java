package protocol.handlers;

import client.FileDescr;
import lombok.extern.slf4j.Slf4j;
import server.ServerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

@Slf4j
public class ListHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerData state) throws IOException {
        Collection<FileDescr> files = state.allFiles();
        out.writeInt(files.size());
        for (FileDescr fd : files) {
            out.writeInt(fd.getId());
            out.writeUTF(fd.getName());
            out.writeLong(fd.getSize());
        }
        out.flush();
    }
}

