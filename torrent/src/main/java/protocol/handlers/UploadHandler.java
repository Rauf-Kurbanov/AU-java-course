package protocol.handlers;

import client.Seeder;
import lombok.extern.slf4j.Slf4j;
import protocol.Protocol;
import protocol.TorrentProtocol;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class UploadHandler implements RequestHandler {

    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerState state) throws IOException {
//            log.info("handling upload");
        String name = in.readUTF();
        long size = in.readLong();
        // TODO does handler depend on actual protocol?
        Protocol protocol = TorrentProtocol.getINSTANCE();
        final InetAddress ip = clientSocket.getInetAddress();
        short port = (short) clientSocket.getPort();
        final Seeder seeder = new Seeder(protocol, ip, port);

        final int newFileId = state.upload(name, size);
        state.addSeeder(newFileId, seeder);

        out.writeInt(newFileId);
        out.flush();
        log.info("leaving upload handler");
    }
}
