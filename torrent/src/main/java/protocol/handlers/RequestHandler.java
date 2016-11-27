package protocol.handlers;

import server.ServerData;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// TODO refactoring
@FunctionalInterface
public interface RequestHandler {

    void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, ServerData state) throws IOException;
}
