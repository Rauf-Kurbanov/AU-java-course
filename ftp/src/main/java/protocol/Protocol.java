package protocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface Protocol {

    void answerQuery(DataInputStream query, DataOutputStream output) throws IOException;

    void answerList(String path, DataOutputStream output) throws IOException;

    void answerGet(String path, DataOutputStream output) throws IOException;

    List<FtpFile> readListResponse(DataInputStream contents) throws IOException;

    void readGetResponse(DataInputStream contents, OutputStream out) throws IOException;
}
