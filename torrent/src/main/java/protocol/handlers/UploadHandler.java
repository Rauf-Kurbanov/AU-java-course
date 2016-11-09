package protocol.handlers;

import client.Seeder;
import protocol.Protocol;
import protocol.TorrentProtocol;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class UploadHandler implements RequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        String name = in.readUTF();
//        long size = in.readLong();
//        out.writeInt(state.upload(name, size));
//
//        Socket clientSocket = new Socket("localhost", clientPort);
//
//        DataInputStream clientIn= new DataInputStream(clientSocket.getInputStream());
//        DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
//
//
//        FileInfo fileInfo = new FileInfo();
//    }

    @Override
    public void handle(Socket clientSocket, ServerState state) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        String name = in.readUTF();
        long size = in.readLong();
//        Seeder seeder = new Seeder(clientSocket.getInetAddress().getAddress(), (short) clientSocket.getPort());
        InetAddress ip = clientSocket.getInetAddress();
        short port = (short) clientSocket.getPort();
        Protocol protocol = new TorrentProtocol();
        Seeder seeder = new Seeder(protocol, ip, port);

        out.writeInt(state.upload(name, size, seeder));
    }
}
