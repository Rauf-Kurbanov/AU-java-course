package client;

import client.ex.FailedUpdateException;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.SeederInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.concurrent.TimeUnit.MINUTES;

// TODO connect?
@Slf4j
public class TorrentClient {

    private static final int UPDATE_TIMEOUT_MIN = 5;

    @NotNull
    private final TorrentProtocol protocol = TorrentProtocol.getINSTANCE();
    private Socket clientSocket;
    // TODO USE
    private final Seeder seeder;
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
//        final oneElenetList = new
        final short seederPort = seeder.getPort();
        protocol.requestUpdate(in, out, seederPort, Collections.singleton(serverFileId));

        fileManager.addToIndex(serverFileId, fileName);
    }

    public List<SeederInfo> sources(int fileId) throws IOException {
        return protocol.requestSources(in, out, fileId);
    }

    private void update() throws IOException {
        final Set<Integer> fileIds = fileManager.allIds();
        final short seederPort = seeder.getPort();
        final boolean status = protocol.requestUpdate(in, out, seederPort, fileIds);

        if (!status) {
            throw new FailedUpdateException();
        }
    }

    private List<Integer> stat(int fileId
            , DataInputStream seederIn, DataOutputStream seederOut) throws IOException {
        log.info("Client stat");
//        return protocol.requestStat(seederIn, seederOut, fileId);
        return protocol.requestStat(seederIn, seederOut, fileId);
    }

    // TODO why do I need whole FileDescr as argument?
    public void getFile(FileDescr fileDescr, SeederInfo seederInfo) throws IOException {
//        log.info(String.format("Client: getFile: %s from %s", fileDescr.toString(), seederInfo.toString()));
        System.out.println("!!!!!!!!!!!!!!!!!!!!Seeder port:" + seederInfo.getPort() + "\n");
        final Socket p2pSocket = new Socket(seederInfo.getInetAddress(), seederInfo.getPort());
        final DataInputStream seederIn = new DataInputStream(p2pSocket.getInputStream());
        final DataOutputStream seederOut = new DataOutputStream(p2pSocket.getOutputStream());
        final int fileId = fileDescr.getId();
        if (!fileManager.isDownloading(fileId)) {
            fileManager.startDownloading(fileDescr);
        }

        final List<Integer> parts = stat(fileId, seederIn, seederOut);
        System.out.println("PARTS:");
        parts.forEach(System.out::println);
        final FileHolder fileHolder = fileManager.getDownloadingFile(fileId);
        for (int partId : parts) {
            // TODO refactor
//            final byte[] content = protocol.requestGet(seederIn, seederOut, fileId, partId);
            final byte[] content = protocol.requestGet(seederIn, seederOut, fileId, partId);
            fileHolder.readPart(partId, new ByteArrayInputStream(content));
        }

        seederIn.close();
        seederOut.close();
        p2pSocket.close();
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
        fileManager = new FileManager(fileSystemRoot);
        seeder = new Seeder(protocol, fileManager);
        seeder.start();
        scheduleUpdate();
    }

    public void disconnect() throws IOException {
        out.writeByte(-1);
        out.flush();
        clientSocket.close();

        timer.cancel();
        timer.purge();
        seeder.stop();
    }
}
