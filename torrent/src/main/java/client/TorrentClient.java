package client;

import client.ex.FailedUpdateException;
import com.sun.istack.internal.NotNull;
import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.SeederInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.util.*;

import static java.util.concurrent.TimeUnit.MINUTES;

// TODO connect?
@Slf4j
public class TorrentClient {

    private static final int UPDATE_TIMEOUT_MIN = 5;

    @NotNull
    private final TorrentProtocol protocol = TorrentProtocol.INSTANCE;
    private Socket clientSocket;
    // TODO USE
    private final Seeder seeder;
    private final Leecher leecher;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Timer timer = new Timer();
    private final Map<Integer, Set<SeederInfo>> knownSeeders = new HashMap<>();

//    private final ExecutorService leecherExecutor = Executors.newSingleThreadExecutor();

    public TorrentClient(String clientIp, int port, Path fileSystemRoot) throws IOException {
        clientSocket = new Socket(InetAddress.getByName(clientIp), port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        final FileManager fileManager = new FileManager(fileSystemRoot);
        seeder = new Seeder(protocol, fileManager);
        seeder.start();

        leecher = new Leecher(fileManager);

        scheduleUpdate();
    }

    public List<FileDescr> list() throws IOException, InterruptedException {
        return protocol.requestList(in, out);
    }

    public void upload(String fileName) throws IOException {
        final long fileSize = seeder.getFileSizeToUpload(fileName);

        final int serverFileId = protocol.requestUpload(in, out, fileName, fileSize);
        final short seederPort = seeder.getPort();
        protocol.requestUpdate(in, out, seederPort, Collections.singleton(serverFileId));

        seeder.addToIndex(serverFileId, fileName);
    }

//    public List<SeederInfo> sources(int fileId) throws IOException {
//        return protocol.requestSources(in, out, fileId);
//    }
//
    public void pullSources(int fileId) throws IOException {
        Set<SeederInfo> seederInfos = knownSeeders.getOrDefault(fileId, new HashSet<>());
        seederInfos.addAll(seederInfos);
        knownSeeders.put(fileId, seederInfos);
    }

    private void update() throws IOException {
        final Set<Integer> fileIds = seeder.allIds();
        final short seederPort = seeder.getPort();
        final boolean status = protocol.requestUpdate(in, out, seederPort, fileIds);

        if (!status) {
            throw new FailedUpdateException();
        }
    }

    private List<Integer> stat(int fileId
            , DataInputStream seederIn, DataOutputStream seederOut) throws IOException {
        log.info("Client stat");
        return protocol.requestStat(seederIn, seederOut, fileId);
    }

    public void getFile(FileDescr fileDescr, SeederInfo seederInfo) throws IOException {
        final Socket p2pSocket;
        p2pSocket = new Socket(seederInfo.getInetAddress(), seederInfo.getPort());
        final DataInputStream seederIn = new DataInputStream(p2pSocket.getInputStream());
        final DataOutputStream seederOut = new DataOutputStream(p2pSocket.getOutputStream());
        final int fileId = fileDescr.getId();
        final List<Integer> parts = stat(fileId, seederIn, seederOut);
        leecher.getPartsFrom(fileDescr, seederInfo, parts);
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

    public FileStatus getStatus(String fileName) {
        return leecher.getStatus(fileName);
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
