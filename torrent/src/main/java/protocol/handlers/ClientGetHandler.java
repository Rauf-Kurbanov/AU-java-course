package protocol.handlers;

import client.ClientState;
import server.FileInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientGetHandler implements ClientRequestHandler {
    @Override
    public void handle(Socket clientSocket, ClientState state) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        int fileId = in.readInt();
        int part = in.readInt();
//        List<Seeder> seeders = state.getSeeders(fileId, part);
//        Seeder seeder = seeders.get(new Random().nextInt(seeders.size()));

//        final InetAddress ip = seeder.getIp();
        FileInfo fileInfo = state.getFileInfo(fileId);
        boolean isLast = part  == fileInfo.getParts().size() - 1;

        byte[] content;
        if (!isLast) {
            content = new byte[FileInfo.PART_SIZE];
        } else {
            final int lastPartSize = (int) fileInfo.getSize() % FileInfo.PART_SIZE;
            content = new byte[lastPartSize];
        }

        out.write(content);
    }

//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        int fileId = in.readInt();
//        int part = in.readInt();
//        byte[] content = null;
//        out.write(content);
//    }
}
