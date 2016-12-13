package client;

import client.ex.FailedUpdateException;
import com.sun.istack.internal.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import protocol.TorrentProtocol;
import server.SeederInfo;
import util.Serializer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static client.FileStatus.READY;
import static java.util.concurrent.TimeUnit.MINUTES;

// TODO connect?
@Slf4j
public class TorrentClient implements Serializable {

    private static final int UPDATE_TIMEOUT_MIN = 5;

    @NotNull
    private final TorrentProtocol protocol = TorrentProtocol.INSTANCE;
    private Socket clientSocket;
    private final Seeder seeder;
    private final Leecher leecher;
    private final DataInputStream in;
    private final DataOutputStream out;
    private final Timer timer = new Timer();
    private final Map<Integer, Set<SeederInfo>> knownSeeders = new HashMap<>();
    private final FileManager fileManager;


    public TorrentClient(String clientIp, int port, FileManager fileManager) throws IOException {
        clientSocket = new Socket(InetAddress.getByName(clientIp), port);
        in = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        this.fileManager = fileManager;
        seeder = new Seeder(protocol, fileManager);
        seeder.start();
        leecher = new Leecher(fileManager);

        scheduleUpdate();
    }

    public TorrentClient(String clientIp, int port, Path fileSystemRoot) throws IOException {
        this(clientIp, port, new FileManager(fileSystemRoot.toString()));
    }

    public List<FileDescr> list() throws IOException, InterruptedException {
        return protocol.requestList(in, out);
    }

    public void upload(String fileName) throws IOException {
        final long fileSize = seeder.getFileSizeToUpload(fileName);

        final int serverFileId = protocol.requestUpload(in, out, fileName, fileSize);
        final short seederPort = seeder.getPort();
        protocol.requestUpdate(in, out, seederPort, Collections.singleton(serverFileId));

        fileManager.addToIndex(serverFileId, fileName);
        fileManager.setReady(fileName);
    }

    public void pullSources(int fileId) throws IOException {
        Set<SeederInfo> seederInfos = knownSeeders.getOrDefault(fileId, new HashSet<>());
        seederInfos.addAll(protocol.requestSources(in, out, fileId));
        knownSeeders.put(fileId, seederInfos);
    }

    private void update() throws IOException {
        final Set<Integer> fileIds = fileManager.allIds();
        final short seederPort = seeder.getPort();
        final boolean status = protocol.requestUpdate(in, out, seederPort, fileIds);

        if (!status) {
            throw new FailedUpdateException();
        }
    }

    public void getFile(FileDescr fileDescr) throws IOException {
        if (!fileManager.isDownloading(fileDescr.getId())) {
            fileManager.startDownloading(fileDescr);
        }
        leecher.getPartsFrom(fileDescr, knownSeeders.get(fileDescr.getId()));
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
        return fileManager.getStatus(fileName);
    }

    public void disconnect() throws IOException {
        out.writeByte(-1);
        out.flush();
        clientSocket.close();

        timer.cancel();
        timer.purge();
        seeder.stop();
    }

    public void pause() throws IOException {
        final Path fileSystemPath = Paths.get(fileManager.root.toString(), "filesystem");
        Serializer.serialize(fileManager, fileSystemPath);
    }

    public static TorrentClient resume(String clientIp, int port, Path fsRoot) throws IOException, ClassNotFoundException {
        final Path fsPath = Paths.get(fsRoot.toString(), "filesystem");
        try (ObjectInputStream ois
                     = new ObjectInputStream(new FileInputStream(fsPath.toFile()))) {
            final FileManager fileManager = (FileManager) ois.readObject();
            return new TorrentClient(clientIp, port, fileManager);
        } catch (ClassNotFoundException | IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public static boolean canResume(Path fsRoot) {
        final Path fsPath = Paths.get(fsRoot.toString(), "filesystem");
        return fsPath.toFile().exists();
    }
}
