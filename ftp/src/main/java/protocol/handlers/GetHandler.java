package protocol.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class GetHandler implements RequestHandler {

    @Override
    public void handle(DataInputStream in, DataOutputStream out) throws IOException {

        String path = in.readUTF();

        File f = new File(path);
        if (!f.exists()) {
            out.writeLong(0);
            return;
        }
        out.writeLong(f.length());
        Files.copy(f.toPath(), out);
    }
}
