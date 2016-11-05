package client;

import protocol.TorrentProtocol;

import java.io.IOException;

public class TorrentClient extends Client {

    private final TorrentProtocol protocol = new TorrentProtocol();

    public TorrentClient(String hostName, int port) throws IOException {
        connect(hostName, port);
    }
}
