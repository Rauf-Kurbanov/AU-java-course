package protocol;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FtpProtocol {

    public void sendListRequest(String path, DataOutputStream output) throws IOException {
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

    public void answerQuery(DataInputStream in, DataOutputStream out) {
        log.info("answerQuery");
        try {
            Command request;
            while ((request = Command.fromInt(in.readInt())) != Command.DISCONNECT) {
                switch (request) {
                    case LIST:
                        formListResponse(in.readUTF(), out);
                        break;
                    case GET:
                        formGetResponse(in.readUTF(), out);
                        break;
                    default:
                        System.out.format("Unknown Command %d", request);
                }
            }
        } catch (IOException e) {
            System.out.println("Socket was closed");
        }
    }

    private void formListResponse(String path, DataOutputStream out) throws IOException {
        log.info("formListResponse");
        File f = new File(path);
        if (!f.exists() || f.isFile()) {
            out.writeInt(0);
            return;
        }
        File[] dir = f.listFiles();
        out.writeInt(dir.length);
        for (File fin : dir) {
            out.writeUTF(fin.getAbsolutePath());
            out.writeBoolean(fin.isDirectory());
        }
    }

    private void formGetResponse(String path, DataOutputStream out) throws IOException {
        log.info("formGetResponse");
        File f = new File(path);
        if (!f.exists()) {
            out.writeLong(0);
            return;
        }
        out.writeLong(f.length());
        Files.copy(f.toPath(), out);
    }
}