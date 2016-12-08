package protocol.handlers;

import client.FileHolder;
import client.FileManager;
import client.Seeder;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

@Slf4j
public class GetHandler implements ClientRequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out
//            , Socket clientSocket, FileManager state) throws IOException {
//        log.info("Handling get\n");
//        final int fileId = in.readInt();
//        final int partId = in.readInt();
//
//        final FileHolder fileHolder = state.getSeededFile(fileId);
//        log.info(String.format("requested partId = %d\n", partId));
//        final byte[] content = fileHolder.getPart(partId);
//        System.out.printf("content length %d\n", content.length);
//        out.write(content);
//        out.flush();
//    }

    @Override
    public void handle(DataInputStream in, DataOutputStream out
            , Socket clientSocket, Seeder state) throws IOException {
        log.info("Handling get\n");
        final int fileId = in.readInt();
        final int partId = in.readInt();

        final FileHolder fileHolder = state.getSeededFile(fileId);
        log.info(String.format("requested partId = %d\n", partId));
        final byte[] content = fileHolder.getPart(partId);
        System.out.printf("content length %d\n", content.length);
        out.write(content);
        out.flush();
    }
}