package protocol.handlers;

import client.Seeder;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class SourcesHandler implements RequestHandler {

    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerState state) throws IOException {
        int fileId = in.readInt();
        final Collection<Seeder> seeders = state.getSeeders(fileId);
        out.writeInt(seeders.size());
        for (final Seeder s : seeders) {
            final byte[] ip = s.getIp().getAddress();
            out.write(ip);
            out.writeShort(s.getPort());
        }
    }
}
