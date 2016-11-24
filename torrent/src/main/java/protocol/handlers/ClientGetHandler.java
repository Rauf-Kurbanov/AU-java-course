package protocol.handlers;

import client.ClientState;
import client.FileHolder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// TODO refactor
public class ClientGetHandler implements ClientRequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ClientState state) throws IOException {
//        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
//        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        int fileId = in.readInt();
        int part = in.readInt();
//        List<Seeder> seeders = state.getSeeders(fileId, part);
//        Seeder seeder = seeders.get(new Random().nextInt(seeders.getFileSize()));

//        final InetAddress ip = seeder.getIp();
        FileHolder fileHolder = state.getFileInfo(fileId);
        boolean isLast = part  == fileHolder.getParts().size() - 1;

        byte[] content;
        if (!isLast) {
            content = new byte[FileHolder.PART_SIZE];
        } else {
            final int lastPartSize = (int) fileHolder.getSize() % FileHolder.PART_SIZE;
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
