package protocol;

import client.FileDescr;
import client.Seeder;
import lombok.Getter;
import protocol.handlers.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TorrentProtocol implements Protocol {

    private static final int PART_SIZE = 10_000_000;

    private TorrentProtocol() {}

    @Getter
    public static final TorrentProtocol INSTANCE = new TorrentProtocol();

    static {
        handlerByCommand.put((byte) 1, new ListHandler());
        handlerByCommand.put((byte) 2, new UploadHandler());
        handlerByCommand.put((byte) 3, new SourcesHandler());
        handlerByCommand.put((byte) 4, new UpdateHandler());

        clientHandlerByCommand.put((byte) 1, new ClientStatHandler());
        clientHandlerByCommand.put((byte) 2, new ClientGetHandler());
    }

    public List<FileDescr> requestList(DataInputStream in, DataOutputStream out) throws IOException {
        out.writeByte(1);

        List<FileDescr> res = new ArrayList<>();
        int count = in.readInt();
        System.out.println("list count = " + Integer.toString(count));
        while (count-- > 0) {
            FileDescr fd = new FileDescr(in.readInt(), in.readUTF(), in.readLong());
            res.add(fd);
        }
        return res;
    }

    public int requestUpload(DataInputStream in, DataOutputStream out,
                             String fileName, long size) throws IOException {
        out.writeByte(2);
        out.writeUTF(fileName);
        out.writeLong(size);

        return in.readInt();
    }

    public List<Seeder> requestSources(DataInputStream in, DataOutputStream out, int fileId) throws IOException {
        out.writeByte(3);
        out.writeInt(fileId);

        List<Seeder> res = new ArrayList<>();
        int size = in.readInt();
        while (size-- > 0) {
            final byte[] ip = new byte[4];
            in.readFully(ip);
            final short port = in.readShort();
            res.add(new Seeder(this, InetAddress.getByAddress(ip), port));
        }
        return res;
    }

    public boolean requestUpdate(DataInputStream in, DataOutputStream out,
                                 short clientPort, final Collection<Integer> fileIds) throws IOException {
        out.writeShort(clientPort);
        out.writeInt(fileIds.size());
        for (int fileId : fileIds) {
            out.writeInt(fileId);
        }

        return in.readBoolean();
    }

    public List<Integer> requestStat(DataInputStream in, DataOutputStream out,
                                     int fileId) throws IOException {
        out.writeByte(1);
        out.writeByte(fileId);

        int count = in.readInt();
        final List<Integer> parts = new ArrayList<>();
        while (count-- > 0) {
            parts.add(in.readInt());
        }
        return parts;
    }

    public byte[] requestGet(DataInputStream in, DataOutputStream out,
                             int fileId, int part) throws IOException {
        out.writeByte(2);
        out.writeInt(fileId);
        out.writeInt(part);

        byte[] content = new byte[PART_SIZE];
        while (in.read(content) != -1) {}
        return content;
    }
}
