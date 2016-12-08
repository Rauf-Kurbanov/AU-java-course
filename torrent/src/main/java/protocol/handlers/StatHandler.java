package protocol.handlers;

import client.FileHolder;
import client.Seeder;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

@Slf4j
public class StatHandler implements ClientRequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out,
//                       Socket clientSocket, FileManager state) throws IOException {
//        log.info("Handling stat");
//        final int fileId = in.readInt();
//        final FileHolder fileHolder = state.getSeededFile(fileId);
//        if (fileHolder == null) {
//            System.out.println("No such file");
//            return;
//        }
//        final List<Integer> parts = fileHolder.getParts();
//        System.out.printf("Number of parts in stat handler: %d\n", parts.size());
//        out.writeInt(parts.size());
//        for (int p : parts) {
//            out.writeInt(p);
//        }
//        out.flush();
//    }

    @Override
    public void handle(DataInputStream in, DataOutputStream out,
                       Socket clientSocket, Seeder state) throws IOException {
        log.info("Handling stat");
        final int fileId = in.readInt();
        final FileHolder fileHolder = state.getSeededFile(fileId);
        if (fileHolder == null) {
            System.out.println("No such file");
            return;
        }
        final List<Integer> parts = fileHolder.getParts();
        System.out.printf("Number of parts in stat handler: %d\n", parts.size());
        out.writeInt(parts.size());
        for (int p : parts) {
            out.writeInt(p);
        }
        out.flush();
    }
}
