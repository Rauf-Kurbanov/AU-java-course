package client;

import com.sun.istack.internal.NotNull;
import protocol.TorrentProtocol;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public class TorrentClient extends Client {

    @NotNull
    private final TorrentProtocol protocol = new TorrentProtocol();
    private ServerSocket serverSocket;
    private Seeder seeder;

    public TorrentClient(String hostName, int port, InetAddress clientIp) throws IOException {
        connect(hostName, port);
        seeder = new Seeder(protocol, clientIp, port);
    }

}
