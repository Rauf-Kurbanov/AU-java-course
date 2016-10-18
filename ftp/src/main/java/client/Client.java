package client;

import protocol.FtpFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface Client {

    void connect(String hostName, int port) throws IOException;

    void disconnect() throws IOException;

    List<FtpFile> executeList(String path) throws IOException;

    void executeGet(String path, OutputStream out) throws IOException;

}
