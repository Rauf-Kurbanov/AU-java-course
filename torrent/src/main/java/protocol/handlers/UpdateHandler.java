package protocol.handlers;

import client.Seeder;
import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UpdateHandler implements RequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out,
                       Socket clientSocket, ServerState state) throws IOException {
        log.info("handling update");

        final short clientPort = in.readShort();
        int count = in.readInt();
        List<Integer> fileIDs = new ArrayList<>();
        while (count-- > 0) {
            fileIDs.add(in.readInt());
        }
        final Seeder seeder = new Seeder(TorrentProtocol.getINSTANCE(), clientSocket.getInetAddress(), clientPort);
        for (int fileId : fileIDs) {
            state.addSeeder(fileId, seeder);
        }

        final boolean status = true;
        out.writeBoolean(status);
    }
}
