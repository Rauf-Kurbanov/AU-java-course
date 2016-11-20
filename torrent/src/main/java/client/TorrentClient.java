package client;

import client.ex.FailedUpdateException;
import com.sun.istack.internal.NotNull;
import protocol.TorrentProtocol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.MINUTES;

// TODO connect?
public class TorrentClient {

    private static final int UPDATE_TIMEOUT_MIN = 5;

    @NotNull
    private final TorrentProtocol protocol = TorrentProtocol.getINSTANCE();
//    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Seeder seeder;
    private final FileSystem fileSystem;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Timer timer = new Timer();

    public List<FileDescr> list() throws IOException, InterruptedException {
        return protocol.requestList(in, out);
    }

    public void upload(String fileName) throws IOException {
        checkArgument(fileSystem.contains(fileName),
                String.format("file %s doesn't exist in %s", fileName, fileSystem.root.getFileName()));

        final long fileSize = fileSystem.getFileSize(fileName);
        final int serverFileId = protocol.requestUpload(in, out, fileName, fileSize);
        fileSystem.addToIndex(serverFileId, fileName);
    }

    public List<Seeder> sources(int fileId) throws IOException {
        return protocol.requestSources(in, out, fileId);
    }

    private void update() throws IOException {
        final Set<Integer> fileIds = fileSystem.allIds();
        final short clientPort = (short) clientSocket.getPort();
        final boolean status = protocol.requestUpdate(in, out, clientPort, fileIds);

        if (!status) {
            throw new FailedUpdateException();
        }
    }

    private void scheduleUpdate() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    update();
                } catch (Exception e) {
                    System.out.printf("Failed to update: %s%n", e.getMessage());
                }
            }
        }, 0, MINUTES.toMillis(UPDATE_TIMEOUT_MIN));
    }

    public TorrentClient(String clientIp, int port, Path fileSystemRoot) throws IOException {
        clientSocket = new Socket(InetAddress.getByName(clientIp), port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        // TODO check port or localPort
        seeder = new Seeder(protocol, InetAddress.getByName(clientIp), port);
        fileSystem = new FileSystem(fileSystemRoot);

        scheduleUpdate();
    }

    public void disconnect() throws IOException {
        out.writeByte(-1);
        out.flush();
        clientSocket.close();
    }
}
