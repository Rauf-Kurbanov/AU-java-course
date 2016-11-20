import client.FileDescr;
import client.TorrentClient;
import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.Server;
import server.ThreadPooledServer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

//        Protocol torrentProtocol = new TorrentProtocol();
        Server torrentServer = new ThreadPooledServer(TorrentProtocol.getINSTANCE());
        final int portNumber = 8081;
        torrentServer.start(portNumber);
        Path testDir = Paths.get("/home/rauf/Programs/semester_3/java/AU-java-course/torrent/testFolder");
        Path fsA = Paths.get(testDir.toString(), "fsA");
//        Path fsB = Paths.get(testDir.toString(), "fsB");
        TorrentClient clientA = new TorrentClient("127.0.0.1", portNumber, fsA);
//        TorrentClient clientB = new TorrentClient("localhost", portNumber, "127.0.0.1", fsB);

        clientA.upload("fileAA");
        Thread.sleep(1000);

        List<FileDescr> listing = clientA.list();
        listing.forEach(System.out::println);
        assert (listing.size() == 1);

//        final List<Seeder> seeders = clientA.sources(clientA.f);
//        System.out.println("seeders amount : " + seeders.size());
//        assert (seeders.size() == 1);

        clientA.disconnect();
        torrentServer.stop();

        System.out.println("the end");
    }
}
