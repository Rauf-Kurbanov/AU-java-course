package protocol;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class FtpProtocolTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final String DIR = "adm/adm";

    private final String F2DIR = DIR + "/1";

    private File file;
    private File dir;


    private ByteArrayOutputStream outContent;
    private DataInputStream inContent;
    private final FtpProtocol protocol = new FtpProtocol();

    private DataOutputStream b;

    @Before
    public void setUpStreams() throws IOException {
        outContent = new ByteArrayOutputStream();
        folder.newFolder(DIR.split("/")[0]);
        folder.newFolder(DIR);
        dir = folder.newFolder(F2DIR);
        String f21 = "short.txt";
        file = folder.newFile(F2DIR + "/" + f21);
        FileUtils.writeByteArrayToFile(file, F2DIR.getBytes());
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());

        b = new DataOutputStream(outContent);
    }

    @After
    public void f() throws IOException {
        outContent.close();
        b.close();
    }

    @Test
    public void sendListRequest() throws Exception {
        protocol.sendListRequest("asd", new DataOutputStream(outContent));

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        int cmd = inContent.readInt();
        String path = inContent.readUTF();
        assertEquals(1, cmd);
        assertEquals("asd", path);
    }

    @Test
    public void sendGetRequest() throws Exception {
        protocol.sendGetRequest("asd", new DataOutputStream(outContent));

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        int cmd = inContent.readInt();
        String path = inContent.readUTF();
        assertEquals(2, cmd);
        assertEquals("asd", path);
    }

    @Test
    public void readListResponse() throws Exception {
        b.writeInt(2);
        b.writeUTF("asd");
        b.writeBoolean(false);
        b.writeUTF("asds");
        b.writeBoolean(true);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        List<FtpFile> lst = protocol.readListResponse(inContent);

        assertEquals(2, lst.size());
        assertEquals("asds", lst.get(1).path);
        assertTrue(lst.get(1).isDir);
    }

    @Test
    public void readGetResponse() throws Exception {
        String content = "asdadasdasdasda";
        b.writeLong(content.getBytes().length);
        b.write(content.getBytes());

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c = new ByteArrayOutputStream();
        protocol.readGetResponse(inContent, new DataOutputStream(c));

        assertEquals(content, c.toString());
    }

    @Test
    public void answerQueryGet() throws Exception {
        String content = file.getAbsolutePath();
        b.writeInt(2);
        b.writeUTF(content);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        ByteArrayOutputStream c2 = new ByteArrayOutputStream();
        protocol.answerQuery(inContent, new DataOutputStream(c1));

        DataOutputStream res = new DataOutputStream(c2);
        res.writeLong(file.length());
        res.writeBytes(F2DIR);
        assertEquals(c2.toString(), c1.toString());

    }

    @Test
    public void nonExistentGetQuery() throws Exception {
        b.writeInt(2);
        b.writeUTF("dont exist");

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        ByteArrayOutputStream c2 = new ByteArrayOutputStream();
        protocol.answerQuery(inContent, new DataOutputStream(c1));

        b = new DataOutputStream(c2);
        b.writeLong(0);
        assertEquals(c2.toString(), c1.toString());
    }

    @Test
    public void answerQueryList() throws Exception {
        String content = dir.getAbsolutePath();
        b.writeInt(1);
        b.writeUTF(content);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        protocol.answerQuery(inContent, new DataOutputStream(c1));

        List<FtpFile> lst = protocol.readListResponse(new DataInputStream(
                new ByteArrayInputStream(c1.toByteArray())));

        assertEquals(file.getAbsolutePath(), lst.get(0).path);
    }

    @Test
    public void answerNonExistentListQuery() throws Exception {
        b.writeInt(1);
        b.writeUTF("don't exist");

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        protocol.answerQuery(inContent, new DataOutputStream(c1));

        List<FtpFile> lst = protocol.readListResponse(new DataInputStream(
                new ByteArrayInputStream(c1.toByteArray())));
        assertNull(lst);
    }

    @Test
    public void answerListFileQuery() throws Exception {
        b.writeInt(1);
        b.writeUTF(file.getPath());

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        protocol.answerQuery(inContent, new DataOutputStream(c1));

        List<FtpFile> lst = protocol.readListResponse(new DataInputStream(
                new ByteArrayInputStream(c1.toByteArray())));

        assertNull(lst);
    }
}