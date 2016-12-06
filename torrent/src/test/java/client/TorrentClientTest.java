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
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TorrentClientTest {

    private Server torrentServer;
    private TorrentClient clientA;
    private TorrentClient clientB;

    private File fileA;
    private File fileB;
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

        fileA = new File(fsA, "fileA");
        fileB = new File(fsA, "fileB");

        final StringBuilder sb = new StringBuilder();
        for (char filler : Arrays.asList('A', 'B', 'C')) {
            final String s = new String(new char[FileHolder.PART_SIZE]).replace('\0', filler);
            sb.append(s);
        }
        FileUtils.write(fileA, "ShortString", "UTF-8");
        FileUtils.write(fileB, sb.toString(), "UTF-8");

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
        clientA.upload(fileA.getName());
        clientA.upload(fileB.getName());
        final List<FileDescr> listing = clientA.list();
        assertEquals(2, listing.size());

        List<FileDescr> listingB = clientB.list();
        assertEquals(2, listingB.size());
        final FileDescr expected = new FileDescr(0, fileA.getName(), (int) fileA.length());
        System.out.println(expected.toString());
        final FileDescr actual = listingB.get(0);
        System.out.println(actual.toString());
        assertEquals(expected, actual);
    }

    @Test
    public void sources() throws Exception {
        clientA.upload(fileA.getName());
        clientA.upload(fileB.getName());

        final List<FileDescr> listingB = clientB.list();
        assertEquals(2, listingB.size());

        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        assertEquals(1, sis.size());
    }

    @Test
    public void getFileLtPartSize() throws Exception {
        clientA.upload(fileA.getName());

        final List<FileDescr> listingB = clientB.list();
        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        final SeederInfo heGotFile = sis.get(0);

        final File seederFile = new File(fileA.getPath());
        final File leecherFile = new File(fsB, fileA.getName());

        clientB.getFile(first, heGotFile);
        assertEquals(FileUtils.readLines(seederFile, "UTF-8")
                , FileUtils.readLines(leecherFile , "UTF-8"));
    }

    @Test
    public void getFileDivByPartSize() throws Exception {
        clientA.upload(fileB.getName());

        final List<FileDescr> listingB = clientB.list();
        final FileDescr first = listingB.get(0);
        final List<SeederInfo> sis = clientB.sources(first.getId());
        final SeederInfo heGotFile = sis.get(0);

        final File seederFile = new File(fileB.getPath());
        final File leecherFile = new File(fsB, fileB.getName());

        clientB.getFile(first, heGotFile);
        assertEquals(FileUtils.readLines(seederFile, "UTF-8")
                , FileUtils.readLines(leecherFile , "UTF-8"));
    }
}