package protocol;

import client.ClientState;
import protocol.handlers.ClientRequestHandler;
import protocol.handlers.RequestHandler;
import server.ServerState;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

// TODO r
public interface Protocol {

    HashMap<Integer, RequestHandler> handlerByCommand = new HashMap<>();
    HashMap<Integer, ClientRequestHandler> clientHandlerByCommand = new HashMap<>();

//    default void answerServerQuery(DataInputStream in, DataOutputStream out, ServerState state) {
//        try {
//            int request;
//            while ((request = in.readInt()) != -1) {
//                if (!handlerByCommand.containsKey(request)) {
//                    System.out.format("Unknown Command %d", request);
//                }
//                handlerByCommand.get(request).handle(in, out, state);
//            }
//        } catch (IOException e) {
//            System.out.println("Socket was closed");
//        }
//    }

    default void answerServerQuery(Socket clientSocket, ServerState state) {
        try {
            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
//            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
            int request;
            while ((request = in.readInt()) != -1) {
                if (!handlerByCommand.containsKey(request)) {
                    System.out.format("Unknown Command %d", request);
                }
//                handlerByCommand.get(request).handle(in, out, state);
                handlerByCommand.get(request).handle(clientSocket, state);
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
    }

    default void answeClientQuery(Socket serverToClientSocket, ClientState clientState) {
        try {
            DataInputStream in = new DataInputStream(serverToClientSocket.getInputStream());
            int request;
            while ((request = in.readInt()) != -1) {
                if (!clientHandlerByCommand.containsKey(request)) {
                    System.out.format("Unknown Command %d", request);
                }
//                clientHandlerByCommand.get(request).handle(in, out);
                clientHandlerByCommand.get(request).handle(serverToClientSocket, clientState);
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }

    }

//    default void answeClientQuery(DataInputStream in, DataOutputStream out) {
//        try {
//            int request;
//            while ((request = in.readInt()) != -1) {
//                if (!clientHandlerByCommand.containsKey(request)) {
//                    System.out.format("Unknown Command %d", request);
//                }
//                clientHandlerByCommand.get(request).handle(in, out);
//            }
//        } catch (IOException e) {
//            System.out.println("Socket was closed");
//        }
//
//    }
}
