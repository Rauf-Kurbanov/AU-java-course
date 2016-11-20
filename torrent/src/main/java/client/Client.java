package client;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class Client {

//    protected Socket clientSocket;
//    protected DataOutputStream netOut;
//    protected DataInputStream netIn;

//    public void connect(String hostName, int port) throws IOException {
//        clientSocket = new Socket(hostName, port);
//        netOut = new DataOutputStream(clientSocket.getOutputStream());
//        netIn = new DataInputStream(clientSocket.getInputStream());
//    }
//
//    public void disconnect() throws IOException {
//        log.info("CALLING DISCONNECT");
//        netOut.writeByte(-1);
//        netOut.flush();
//        clientSocket.close();
//    }
}
