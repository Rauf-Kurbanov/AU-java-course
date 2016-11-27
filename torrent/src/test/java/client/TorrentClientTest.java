package client;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import protocol.TorrentProtocol;
import server.SeederInfo;
import server.Server;
import server.ThreadPooledServer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TorrentClientTest {

    private Server torrentServer;
    private TorrentClient clientA;
    private TorrentClient clientB;

    @Before
    public void setUp() throws Exception {
        torrentServer = new ThreadPooledServer(TorrentProtocol.getINSTANCE());
        final int portNumber = 8081;
        torrentServer.start(portNumber);
        Path testDir = Paths.get("/home/rauf/Programs/semester_3/java/AU-java-course/torrent/testFolder");
        Path fsA = Paths.get(testDir.toString(), "fsA");
        clientA = new TorrentClient("127.0.0.1", portNumber, fsA);

        Path fsB = Paths.get(testDir.toString(), "fsB");
        clientB = new TorrentClient("127.0.0.1", portNumber, fsB);
    }

    @After
    public void tearDown() throws Exception {
        clientA.disconnect();
        clientB.disconnect();
        torrentServer.stop();
    }

    @Test
    public void uploadList() throws Exception {
        clientA.upload("fileAA");
        clientA.upload("fileAA_copy");
        final List<FileDescr> listing = clientA.list();
        assertEquals(2, listing.size());

        List<FileDescr> listingB = clientB.list();
        assertEquals(2, listingB.size());
        assertEquals(new FileDescr(0, "fileAA", 6844), listingB.get(0));
    }

    @Test
    public void sources() throws Exception {
        clientA.upload("fileAA");
        clientA.upload("fileAA_copy");

        final List<FileDescr> listingB = clientB.list();
        assertEquals(2, listingB.size());

        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        assertEquals(1, sis.size());    
    }

    @Test
    public void getFile() throws Exception {
        clientA.upload("fileAA");
        clientA.upload("fileAA_copy");

        final List<FileDescr> listingB = clientB.list();
        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        final SeederInfo heGotFile = sis.get(0);

        final File fileA = new File("/home/rauf/Programs/semester_3/java/AU-java-course/torrent/testFolder/fsA/fileAA");
        final File fileB = new File("/home/rauf/Programs/semester_3/java/AU-java-course/torrent/testFolder/fsB/fileAA");
        fileB.delete();

        clientB.getFile(first, heGotFile);
        assertTrue(FileUtils.contentEquals(fileA, fileB));
    }

}