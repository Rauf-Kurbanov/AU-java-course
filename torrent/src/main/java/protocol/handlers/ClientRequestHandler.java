package protocol.handlers;

import client.ClientState;

import java.io.IOException;
import java.net.Socket;

// TODO refactoring
@FunctionalInterface
public interface ClientRequestHandler {

    //    void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException;
    void handle(Socket clientSocket, ClientState state) throws IOException;
}
