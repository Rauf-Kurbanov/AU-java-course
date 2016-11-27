package protocol.handlers;

import server.SeederInfo;
import server.ServerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class SourcesHandler implements RequestHandler {

    @Override
    public void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerData state) throws IOException {
        int fileId = in.readInt();
        final Collection<SeederInfo> seederInfos = state.getSeeders(fileId);
        out.writeInt(seederInfos.size());
        for (final SeederInfo s : seederInfos) {
            final byte[] ip = s.getInetAddress().getAddress();
            out.write(ip);
            final short port = s.getPort();
            System.out.printf("port value in hander: %d\n", port);
            out.writeShort(port);
            out.flush();
        }
    }
}
