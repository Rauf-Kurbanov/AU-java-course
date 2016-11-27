package protocol.handlers;

import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.SeederInfo;
import server.ServerData;

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
                       Socket clientSocket, ServerData state) throws IOException {
        log.info("handling update");

        final short seederPort = in.readShort();
        int count = in.readInt();
        List<Integer> fileIDs = new ArrayList<>();
        while (count-- > 0) {
            fileIDs.add(in.readInt());
        }
        final SeederInfo seederInfo = new SeederInfo(TorrentProtocol.getINSTANCE()
                , clientSocket.getInetAddress(), seederPort);
        for (int fileId : fileIDs) {
            state.addSeeder(fileId, seederInfo);
        }

        final boolean status = true;
        out.writeBoolean(status);
        out.flush();
    }
}
