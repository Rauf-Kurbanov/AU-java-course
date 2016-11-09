package protocol.handlers;

import client.ClientState;
import client.Seeder;
import protocol.Protocol;
import protocol.TorrentProtocol;
import server.FileInfo;
import server.ServerState;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UpdateHandler implements RequestHandler {
//    @Override
//    public void handle(DataInputStream in, DataOutputStream out, ServerState state) throws IOException {
//        short clientPort = in.readShort();
//        int count = in.readInt();
//        Socket clientSocket = new Socket("localhost", clientPort);
//        DataInputStream clientIn= new DataInputStream(clientSocket.getInputStream());
//        DataOutputStream clientOut = new DataOutputStream(clientSocket.getOutputStream());
//        while (count-- > 0) {
//            int fileId = in.readInt();
//            Protocol protocol = new TorrentProtocol();
//            protocol.answeClientQuery(clientIn, clientOut);
//            clientOut.writeByte(1);
//            clientOut.writeInt(fileId);
//
//            int partCount = in.readInt();
//            ArrayList<Integer> parts = new ArrayList<>();
//            while (partCount-- > 0) {
//                parts.add(in.readInt());
//            }
//            // what if parts is empty?
//            Seeder seeder = new Seeder(new byte[]{127, 0, 0, 1}, clientPort);
//            FileInfo fileInfo = state.getFilesBySeeder().get(seeder);
//            fileInfo.setParts(parts);
//
//            out.writeBoolean(true);
//        }
//    }

    @Override
    public void handle(Socket clientSocket, ServerState state) throws IOException {
        DataInputStream in = new DataInputStream(clientSocket.getInputStream());
        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

        short clientPort = in.readShort();
        int count = in.readInt();
        // TODO change localhost to actual ip
        InetAddress ip = clientSocket.getInetAddress();
        Socket serverToClientSocket = new Socket(ip, clientPort);
        DataInputStream clientIn = new DataInputStream(serverToClientSocket.getInputStream());
        DataOutputStream clientOut = new DataOutputStream(serverToClientSocket.getOutputStream());

        List<Integer> fileIDs = new ArrayList<>();
        while (count-- > 0) {
            fileIDs.add(in.readInt());
        }
        List<FileInfo> fileInfos = new ArrayList<>();
        Protocol protocol = new TorrentProtocol();
        for (int fileId : fileIDs) {
            ClientState clientState = null; // TODO
            protocol.answeClientQuery(serverToClientSocket, clientState);
            clientOut.writeByte(1);
            clientOut.writeInt(fileId);

            int partCount = in.readInt();
            ArrayList<Integer> parts = new ArrayList<>();
            while (partCount-- > 0) {
                parts.add(in.readInt());
            }
            FileInfo fileInfo = state.getFileInfo(fileId);
            fileInfo.setParts(parts);
            fileInfos.add(fileInfo);
        }
        Seeder seeder = new Seeder(protocol, clientSocket.getInetAddress(), clientPort);
        state.setFiles(seeder, fileInfos);

        out.writeBoolean(true);
    }
}
