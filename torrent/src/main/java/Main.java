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

//    public static void main(String[] args) throws IOException {
//        final Path testDir = Paths.get("/home/rauf/Programs/semester_3/java/AU-java-course/torrent/testFolder");
//        final Path file = Paths.get(testDir.toString(), "fsA/fileAA");
//
//        final InputStream in = new FileInputStream(file.toFile());
//        final int BUFF_SIZE = 10_000_000;
//        final byte[] content = new byte[BUFF_SIZE];
//        while (in.read(content) != -1) {}
//
//        final int PART_SIZE = 2_000;
//        final byte[] part1 = new byte[PART_SIZE];
//        final byte[] part2 = new byte[BUFF_SIZE - PART_SIZE];
//        System.arraycopy(content, 0, part1, 0, PART_SIZE);
//        System.arraycopy(content, PART_SIZE, part2, 0, part2.length);
//
//        final Path toWrite = Paths.get(testDir.toString(), "fsA/fileAA_copy");
//        FileOutputStream fos = new FileOutputStream(toWrite.toFile());
//        fos.write(part1);
//        fos.write(part2);
//    }
}
