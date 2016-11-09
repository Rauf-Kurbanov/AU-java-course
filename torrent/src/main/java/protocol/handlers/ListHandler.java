package protocol.handlers;

import server.FileInfo;
import server.ServerState;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class ListHandler implements RequestHandler {
    @Override
    public void handle(Socket clientSocket, ServerState state) throws IOException {
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        Collection<FileInfo> files = state.allFiles();
        out.writeInt(files.size());
        for (FileInfo fi : files) {
            out.writeUTF(fi.getName());
            out.writeLong(fi.getSize());
        }
    }
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        Collection<FileInfo> files = state.getFilesById().values();
//        out.writeInt(files.size());
//        for (FileInfo fi : files) {
//            out.writeUTF(fi.getName());
//            out.writeLong(fi.getSize());
//        }
//    }
}

