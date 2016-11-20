package protocol;

import client.ClientState;
import protocol.handlers.ClientRequestHandler;
import protocol.handlers.RequestHandler;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

// TODO r
public interface Protocol {

    HashMap<Byte, RequestHandler> handlerByCommand = new HashMap<>();
    HashMap<Byte, ClientRequestHandler> clientHandlerByCommand = new HashMap<>();

    default void answerServerQuery(Socket clientSocket, ServerState state) {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            byte request;
            while ((request = in.readByte()) != -1) {
                if (!handlerByCommand.containsKey(request)) {
                    System.out.format("Unknown Command %d\n", request);
                }
                handlerByCommand.get(request).handle(in, out, clientSocket, state);
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
    }

    default void answerClientQuery(Socket serverToClientSocket, ClientState clientState) {
        try {
            DataInputStream in = new DataInputStream(serverToClientSocket.getInputStream());
            int request;
            while ((request = in.readByte()) != -1) {
                if (!clientHandlerByCommand.containsKey(request)) {
                    System.out.format("Unknown Command %d\n", request);
                }
//                clientHandlerByCommand.get(request).handle(in, out);
                clientHandlerByCommand.get(request).handle(serverToClientSocket, clientState);
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }

    }
}
