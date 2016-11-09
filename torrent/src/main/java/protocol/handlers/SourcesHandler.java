package protocol.handlers;

import client.Seeder;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class SourcesHandler implements RequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        int fileId = in.readInt();
//        Collection<Seeder> seeders = state.getSeedersByFile().get(fileId);
//        out.writeInt(seeders.size());
//        for (Seeder s : seeders) {
//            for (byte b : s.getIp()) {
//                out.writeByte(b);
//            }
//            out.writeShort(s.getPort());
//        }
//    }

    @Override
    public void handle(Socket clientSocket, ServerState state) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        int fileId = in.readInt();
        List<Seeder> seeders = state.getSeeders(fileId);
        out.writeInt(seeders.size());
        for (Seeder s : seeders) {
            for (byte b : s.getIp().getAddress()) {
                out.writeByte(b);
            }
            out.writeShort(s.getPort());
        }
    }
}
