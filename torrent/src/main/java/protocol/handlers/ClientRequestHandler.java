package protocol.handlers;

import client.Seeder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// TODO refactoring
@FunctionalInterface
public interface ClientRequestHandler {

    //    void handle(DataInputStream in, DataOutputStream out, ServerData state) throws IOException;
//    void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, FileManager state) throws IOException;
    void handle(DataInputStream in, DataOutputStream out, Socket clientSocket, Seeder state) throws IOException;
}
