package protocol;

import protocol.handlers.RequestHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;

public interface Protocol {

    HashMap<Integer, RequestHandler> handlerByCommand = new HashMap<>();

    default void answerQuery(DataInputStream in, DataOutputStream out) {
        try {
            int request;
            while ((request = in.readInt()) != -1) {
                if (!handlerByCommand.containsKey(request)) {
                    System.out.format("Unknown Command %d", request);
                }
                handlerByCommand.get(request).handle(in, out);
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
    }
}
