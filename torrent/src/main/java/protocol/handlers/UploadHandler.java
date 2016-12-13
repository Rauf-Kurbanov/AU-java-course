package protocol.handlers;

import lombok.extern.slf4j.Slf4j;
import server.ServerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class UploadHandler implements RequestHandler {

    @Override
        public void handle(DataInputStream in, DataOutputStream out
            , Socket clientSocket, ServerData state) throws IOException {
        log.info("handling upload");
        String name = in.readUTF();
        long size = in.readLong();
        final int newFileId = state.upload(name, size);

        out.writeInt(newFileId);
        out.flush();
        log.info("leaving upload handler");
    }
}
