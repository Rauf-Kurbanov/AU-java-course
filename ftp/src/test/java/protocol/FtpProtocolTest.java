package protocol;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class FtpProtocolTest {
    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private final String s1 = "adm";
    private final String DIR = "adm/adm";

    private final String F1 = "long.txt";
    private final String F2 = "short.txt";
    private final String F2DIR = DIR + "/1";

    private File f;
    private File f2;
    private File dir;


    private ByteArrayOutputStream outContent;
    private DataInputStream inContent;
    private Socket sock;
    private final Protocol prot = new FtpProtocol();

    @Before
    public void setUpStreams() throws IOException {
        outContent = new ByteArrayOutputStream();
        folder.newFolder(DIR.split("/")[0]);
        folder.newFolder(DIR);
        f = folder.newFile(DIR + "/" + F1);
        dir = folder.newFolder(F2DIR);
        f2 = folder.newFile(F2DIR + "/" + F2);
        FileUtils.writeByteArrayToFile(f2, F2DIR.getBytes());
        System.setProperty("user.dir", folder.getRoot().getAbsolutePath());
    }

    @Test
    public void answerList() throws Exception {
        prot.answerList("asd", new DataOutputStream(outContent));

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        int cmd = inContent.readInt();
        String path = inContent.readUTF();
        assertEquals(1, cmd);
        assertEquals("asd", path);
    }

    @Test
    public void answerGet() throws Exception {
        prot.answerGet("asd", new DataOutputStream(outContent));

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));
        int cmd = inContent.readInt();
        String path = inContent.readUTF();
        assertEquals(2, cmd);
        assertEquals("asd", path);
    }

    @Test
    public void readListResponse() throws Exception {
        DataOutputStream b = new DataOutputStream(outContent);
        b.writeInt(2);
        b.writeUTF("asd");
        b.writeBoolean(false);
        b.writeUTF("asds");
        b.writeBoolean(true);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        List<FtpFile> lst = prot.readListResponse(inContent);

        assertEquals(2, lst.size());
        assertEquals("asds", lst.get(1).path);
        assertTrue(lst.get(1).isDir);
    }

    @Test
    public void readGetResponse() throws Exception {
        DataOutputStream b = new DataOutputStream(outContent);
        String content = "asdadasdasdasda";
        b.writeLong(content.getBytes().length);
        b.write(content.getBytes());

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c = new ByteArrayOutputStream();
        prot.readGetResponse(inContent, new DataOutputStream(c));

        assertEquals(content, c.toString());
    }

    @Test
    public void answerQueryGet() throws Exception {
        DataOutputStream b = new DataOutputStream(outContent);
        String content = f2.getAbsolutePath();
        b.writeInt(2);
        b.writeUTF(content);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        ByteArrayOutputStream c2 = new ByteArrayOutputStream();
        prot.answerQuery(inContent, new DataOutputStream(c1));

        b = new DataOutputStream(c2);
        b.writeLong(f2.length());
        b.writeBytes(F2DIR);
        assertEquals(c2.toString(), c1.toString());
    }

    @Test
    public void answerQueryList() throws Exception {
        DataOutputStream b = new DataOutputStream(outContent);
        String content = dir.getAbsolutePath();
        b.writeInt(1);
        b.writeUTF(content);

        inContent = new DataInputStream(new ByteArrayInputStream(outContent.toByteArray()));

        ByteArrayOutputStream c1 = new ByteArrayOutputStream();
        ByteArrayOutputStream c2 = new ByteArrayOutputStream();
        prot.answerQuery(inContent, new DataOutputStream(c1));

        List<FtpFile> lst = prot.readListResponse(new DataInputStream(
                new ByteArrayInputStream(c1.toByteArray())));

        assertEquals(f2.getAbsolutePath(), lst.get(0).path);
    }

}