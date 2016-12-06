package client;

import lombok.NoArgsConstructor;
import protocol.FtpFile;
import protocol.FtpProtocol;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@NoArgsConstructor
public class FtpClient extends Client {

    private final FtpProtocol protocol = new FtpProtocol();

    public FtpClient(String hostName, int port) throws IOException {
        connect(hostName, port);
    }

    public List<FtpFile> executeList(String path) throws IOException {
        protocol.sendListRequest(path, netOut);
        return protocol.readListResponse(netIn);
    }

    public void executeGet(String path, OutputStream out) throws IOException {
        protocol.sendGetRequest(path, netOut);
        protocol.readGetResponse(netIn, out);
    }
}
