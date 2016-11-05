package protocol.handlers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface ClientRequestHandler {

    void handle(DataInputStream in, DataOutputStream out) throws IOException;
}

