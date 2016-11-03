package protocol;

import lombok.extern.slf4j.Slf4j;
import protocol.handlers.GetHandler;
import protocol.handlers.ListHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FtpProtocol implements Protocol {

    {
        handlerByCommand.put(1, new ListHandler());
        handlerByCommand.put(2, new GetHandler());
    }

    public void sendListRequest(String path, DataOutputStream output) throws IOException {
        log.info("sendListRequest");
        output.writeInt(1);
        output.writeUTF(path);
    }

    public void sendGetRequest(String path, DataOutputStream output) throws IOException {
        log.info("sendGetRequest");
        output.writeInt(2);
        output.writeUTF(path);
    }

    public List<FtpFile> readListResponse(DataInputStream input) throws IOException {
        log.info("readListResponse");
        int size = input.readInt();
        if (size == 0) {
            return null;
        }
        List<FtpFile> list = new ArrayList<>();
        for (int i = 0; i < size; ++i) {
            list.add(new FtpFile(input.readUTF(), input.readBoolean()));
        }
        return list;
    }

    public void readGetResponse(DataInputStream input, OutputStream out) throws IOException {
        log.info("readGetResponse");

        long size = input.readLong();
        if (size == 0) {
            return;
        }
        byte[] buffer = new byte[1000000];
        int len;
        int bytesToRead = (long) buffer.length > size ? (int) size : buffer.length;
        while (bytesToRead > 0 && (len = input.read(buffer, 0, bytesToRead)) != -1) {
            out.write(buffer, 0, len);
            size -= (long) len;
            bytesToRead = (long) len > size ? (int) size : len;
        }
    }
}