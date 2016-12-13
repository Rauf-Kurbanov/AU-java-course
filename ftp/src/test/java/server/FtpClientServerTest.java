package server;

import client.FtpClient;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import protocol.FtpFile;
import protocol.FtpProtocol;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FtpClientServerTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final String DIR = "adm/adm";

    private final String F2DIR = DIR + "/1";

    private File file;
    private File dir;

    private ByteArrayOutputStream outContent;

    static final int portNumber = 1234;
    private ThreadPooledServer server;
    private FtpClient client;

    @Before
    public void setUp() throws IOException, InterruptedException {
        outContent = new ByteArrayOutputStream();
        folder.newFolder(DIR.split("/")[0]);
        folder.newFolder(DIR);
        dir = folder.newFolder(F2DIR);
        String f21 = "short.txt";
        file = folder.newFile(F2DIR + "/" + f21);
        FileUtils.writeByteArrayToFile(file, F2DIR.getBytes());
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());

        server = new ThreadPooledServer(new FtpProtocol());
        server.start(portNumber);

        Thread.sleep(500);
        client = new FtpClient("localhost", portNumber);
    }

    @After
    public void stopAndDisconnect() throws IOException, InterruptedException {
        client.disconnect();
        server.stop();
    }

    @Test
    public void singeClientSingleRequest() throws IOException {
        client.executeGet(file.getPath(), outContent);
        assertEquals(F2DIR, outContent.toString());
    }

    @Test
    public void singeClientMultipleRequests() throws Exception {
        client.executeGet(file.getPath(), outContent);
        assertEquals(F2DIR, outContent.toString());

        List<FtpFile> lst = client.executeList(dir.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), lst.get(0).path);

        outContent.reset();
        client.executeGet(lst.get(0).path, outContent);
        assertEquals(F2DIR, outContent.toString());
    }

    @Test
    public void multipleClientsSingleRequests() throws Exception {
        FtpClient clientB = new FtpClient("localhost", portNumber);
        FtpClient clientC = new FtpClient("localhost", portNumber);

        client.executeGet(file.getPath(), outContent);
        assertEquals(F2DIR, outContent.toString());

        List<FtpFile> lst = clientB.executeList(dir.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), lst.get(0).path);

        outContent = new ByteArrayOutputStream();
        clientC.executeGet(lst.get(0).path, outContent);
        assertEquals(F2DIR, outContent.toString());
    }

    @Test
    public void multipleClientsMultipleRequests() throws Exception {
        FtpClient clientB = new FtpClient("localhost", portNumber);
        FtpClient clientC = new FtpClient("localhost", portNumber);

        client.executeGet(file.getPath(), outContent);
        assertEquals(F2DIR, outContent.toString());

        List<FtpFile> lst = client.executeList(dir.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), lst.get(0).path);

        List<FtpFile> lstB = clientB.executeList(dir.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), lstB.get(0).path);

        lstB = clientB.executeList(dir.getAbsolutePath());
        assertEquals(file.getAbsolutePath(), lstB.get(0).path);

        outContent.reset();
        clientC.executeGet(lst.get(0).path, outContent);
        assertEquals(F2DIR, outContent.toString());

        outContent.reset();
        clientC.executeGet(lst.get(0).path, outContent);
        assertEquals(F2DIR, outContent.toString());
    }

    @Test
    public void nonExistentListTest() throws Exception {
        List<FtpFile> lst = client.executeList("don't exist");
        assertNull(lst);
    }

    @Test
    public void listFileTest() throws Exception {
        List<FtpFile> lst = client.executeList(file.getPath());
        assertNull(lst);
    }

    @Test
    public void nonExistentGetTest() throws Exception {
        client.executeGet("don't exist", outContent);
        assertEquals(0, outContent.size());
    }
}