package client;

import client.ex.FailedUpdateException;
import com.sun.istack.internal.NotNull;
import protocol.TorrentProtocol;

import java.io.*;
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
    private Socket clientSocket;
    private Seeder seeder;
    private final FileManager fileManager;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Timer timer = new Timer();

    public List<FileDescr> list() throws IOException, InterruptedException {
        return protocol.requestList(in, out);
    }

    public void upload(String fileName) throws IOException {
        checkArgument(fileManager.contains(fileName),
                String.format("file %s doesn't exist in %s", fileName, fileManager.root.getFileName()));

        final long fileSize = fileManager.getFileSize(fileName);
        final int serverFileId = protocol.requestUpload(in, out, fileName, fileSize);
        fileManager.addToIndex(serverFileId, fileName);
    }

    public List<Seeder> sources(int fileId) throws IOException {
        return protocol.requestSources(in, out, fileId);
    }

    private void update() throws IOException {
        final Set<Integer> fileIds = fileManager.allIds();
        final short clientPort = (short) clientSocket.getPort();
        final boolean status = protocol.requestUpdate(in, out, clientPort, fileIds);

        if (!status) {
            throw new FailedUpdateException();
        }
    }

    public List<Integer> stat(int fileId) throws IOException {
        return protocol.requestStat(in, out, fileId);
    }


    // TODO why do I need whole FileDescr as argument?
    public void getFile(FileDescr fileDescr) throws IOException {
        final List<Integer> parts = stat(fileDescr.getId());
        final int fileId = fileDescr.getId();
        if (!fileManager.isDownloading(fileId)) {
            fileManager.startDownloading(fileDescr);
        }
        final FileHolder fileHolder = fileManager.getDownloadedFile(fileId);
        for (int partId : parts) {
            fileHolder.readPart(partId, in);
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
        fileManager = new FileManager(fileSystemRoot);

        scheduleUpdate();
    }



    public void disconnect() throws IOException {
        out.writeByte(-1);
        out.flush();
        clientSocket.close();
    }
}
