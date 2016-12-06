package client;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import protocol.TorrentProtocol;
import server.SeederInfo;
import server.Server;
import server.ThreadPooledServer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class LargeFileTest {


    private Server torrentServer;
    private TorrentClient clientA;
    private TorrentClient clientB;

    private File largeFile;
    private File fsA;
    private File fsB;

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    @Before
    public void setUp() throws Exception {
        torrentServer = new ThreadPooledServer(TorrentProtocol.getINSTANCE());
        final int portNumber = 8081;
        torrentServer.start(portNumber);
        tmpFolder.create();

        fsA = tmpFolder.newFolder("fsA");
        fsB = tmpFolder.newFolder("fsB");

        largeFile = new File(fsA, "largeFile");

        Random rand = new Random();
        FileOutputStream fos = new FileOutputStream(largeFile);
        byte[] tenMb = new byte[10_000_000];
        int LOAD = 20;
        while (LOAD-- > 0) {
            rand.nextBytes(tenMb);
            fos.write(tenMb);
        }

        clientA = new TorrentClient("127.0.0.1", portNumber, fsA.toPath());
        clientB = new TorrentClient("127.0.0.1", portNumber, fsB.toPath());
    }

    @After
    public void tearDown() throws Exception {
        clientA.disconnect();
        clientB.disconnect();
        torrentServer.stop();
    }

    @Test
    public void uploadList() throws Exception {
        clientA.upload(largeFile.getName());
        final List<FileDescr> listing = clientA.list();
        assertEquals(1, listing.size());

        List<FileDescr> listingB = clientB.list();
        assertEquals(1, listingB.size());
        final FileDescr expected = new FileDescr(0, largeFile.getName(), (int) largeFile.length());
        System.out.println(expected.toString());
        final FileDescr actual = listingB.get(0);
        System.out.println(actual.toString());
        assertEquals(expected, actual);
    }

    @Test
    public void sources() throws Exception {
//        clientA.upload(largeFile.getName());
        clientA.upload(largeFile.getName());

        final List<FileDescr> listingB = clientB.list();
        assertEquals(1, listingB.size());

        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        assertEquals(1, sis.size());
    }

    @Test
    public void getFile() throws Exception {
        clientA.upload(largeFile.getName());

        final List<FileDescr> listingB = clientB.list();
        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        final SeederInfo heGotFile = sis.get(0);

        final File seederFile = new File(largeFile.getPath());
        final File leecherFile = new File(fsB, largeFile.getName());

        clientB.getFile(first, heGotFile);
        assertEquals(FileUtils.readLines(seederFile, "UTF-8")
                , FileUtils.readLines(leecherFile , "UTF-8"));
    }

}
