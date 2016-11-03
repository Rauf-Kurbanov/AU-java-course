package protocol.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class ListHandler implements RequestHandler {

    @Override
    public void handle(DataInputStream in, DataOutputStream out) throws IOException {

        String path = in.readUTF();
        File f = new File(path);
        if (!f.exists() || f.isFile()) {
            out.writeInt(0);
            return;
        }
        File[] dir = f.listFiles();
        out.writeInt(dir.length);
        for (File fin : dir) {
            out.writeUTF(fin.getAbsolutePath());
            out.writeBoolean(fin.isDirectory());
        }
    }
}
