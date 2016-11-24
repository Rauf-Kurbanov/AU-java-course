package protocol.handlers;

import client.FileHolder;
import lombok.extern.slf4j.Slf4j;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

@Slf4j
public class ListHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerState state) throws IOException {
//            log.info("list handler");
        Collection<FileHolder> files = state.allFiles();
        out.writeInt(files.size());
        for (FileHolder fh : files) {
            out.writeInt(fh.getId());
            out.writeUTF(fh.getName());
            out.writeLong(fh.getSize());
        }
        out.flush();
//            log.info("leaving list handler");
    }
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        Collection<FileHolder> files = state.getFilesById().values();
//        out.writeInt(files.getFileSize());
//        for (FileHolder fi : files) {
//            out.writeUTF(fi.getName());
//            out.writeLong(fi.getSize());
//        }
//    }
}

