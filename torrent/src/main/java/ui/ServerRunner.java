package ui;

import client.FileHolder;
import client.TorrentClient;
import org.apache.commons.io.FileUtils;
import protocol.TorrentProtocol;
import server.ThreadPooledServer;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ServerRunner {

    public static final int portNumber = 8081;
    public static final String dataHome = "/home/esengie/rauf/AU-java-course/torrent/test_data";

    public static void main(String[] args) throws IOException {
        ThreadPooledServer torrentServer = new ThreadPooledServer(TorrentProtocol.INSTANCE);
        torrentServer.start(portNumber);


        final File fsA = new File(dataHome, "fsA");

        final File fileA = new File(fsA, "fileA");
        final File fileB = new File(fsA, "fileB");

        final StringBuilder sb = new StringBuilder();
        for (char filler : Arrays.asList('A', 'B', 'C')) {
            final String s = new String(new char[FileHolder.PART_SIZE]).replace('\0', filler);
            sb.append(s);
        }
        FileUtils.write(fileA, "ShortString", "UTF-8");
        FileUtils.write(fileB, sb.toString(), "UTF-8");

        final TorrentClient clientA = new TorrentClient("127.0.0.1", portNumber, fsA.toPath());

        clientA.upload(fileA.getName());
        clientA.upload(fileB.getName());

    }
}
