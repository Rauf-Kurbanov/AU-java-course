package protocol.handlers;

import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@FunctionalInterface
public interface RequestHandler {

    void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException;
}
