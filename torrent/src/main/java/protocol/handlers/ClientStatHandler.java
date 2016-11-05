package protocol.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;

public class ClientStatHandler implements ClientRequestHandler {
    @Override
    public void handle(DataInputStream in, DataOutputStream out) throws IOException {
        int fileId = in.readInt();
        Collection<Integer> parts = null;
        out.writeInt(parts.size());
        for (int p : parts) {
            out.writeInt(p);
        }
    }

    private class Part{

    }
}
