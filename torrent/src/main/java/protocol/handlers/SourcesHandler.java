package protocol.handlers;

import server.Seeder;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class SourcesHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
        int fileId = in.readInt();
        Collection<Seeder> seeders = state.getSeedersByFile().get(fileId);
        out.writeInt(seeders.size());
        for (Seeder s : seeders) {
            for (byte b : s.getIp()) {
                out.writeByte(b);
            }
            out.writeShort(s.getPort());
        }
    }
}
