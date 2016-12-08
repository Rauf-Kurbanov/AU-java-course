package client;

import lombok.RequiredArgsConstructor;
import protocol.TorrentProtocol;
import server.SeederInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static client.FileStatus.NOT_READY;

@RequiredArgsConstructor
public class Leecher {

    private final FileManager fileManager;
    private final ExecutorService leecherExecutor = Executors.newSingleThreadExecutor();
    private final TorrentProtocol protocol = TorrentProtocol.INSTANCE;
    private volatile boolean active = true;

//    private Map<Integer, FileHolder> files = fileManager.getFiles();
//    private Map<String, FileStatus> status = fileManager.getStatus();

//    private final Map<Integer, FileHolder> downloadingFiles = new ConcurrentHashMap<>();
//    private final Map<String, FileStatus> status = new ConcurrentHashMap<>();

    // TODO move to holder?
    private List<Integer> absentBlocks(FileDescr fileDescr) {
        final FileHolder fileHolder = files().get(fileDescr.getId());
        final int nParts = fileHolder.getNParts();
        final List<Integer> allParts = IntStream.range(0, nParts).boxed().collect(Collectors.toList());
        allParts.removeAll(fileHolder.getParts());
        return allParts;
    }

    private int nRemainingBlocks(FileDescr fileDescr) {
        final FileHolder fileHolder = files().get(fileDescr.getId());
        return fileHolder.getNParts() - fileHolder.getAcquiredParts();
    }

    public void getPartsFrom(FileDescr fileDescr, SeederInfo seederInfo, List<Integer> parts) throws IOException {
        leecherExecutor.execute(() -> {
            final Socket p2pSocket;
            try {
                p2pSocket = new Socket(seederInfo.getInetAddress(), seederInfo.getPort());
                final DataInputStream seederIn = new DataInputStream(p2pSocket.getInputStream());
                final DataOutputStream seederOut = new DataOutputStream(p2pSocket.getOutputStream());
                final int fileId = fileDescr.getId();

                if (!isDownloading(fileId)) {
                    startDownloading(fileDescr);
                }

                System.out.println("PARTS:");
                parts.forEach(System.out::println);
                final FileHolder fileHolder = getDownloadingFile(fileId);
                // TODO ignored exception
                // TODO use future

                for (int partId : parts) {
                    // TODO refactor
                    final byte[] content = protocol.requestGet(seederIn, seederOut, fileId, partId);
                    final FileStatus status = fileHolder.readPart(partId, new ByteArrayInputStream(content));
                    setStatus(fileHolder.getName(), status);
                }

                seederIn.close();
                seederOut.close();
                p2pSocket.close();
            } catch (IOException ingnored) {
                // We will download this part next time
            }

        });

    }

    public void getPartsFrom(FileDescr fileDescr, Collection<SeederInfo> sources, List<Integer> parts) throws IOException {
        Iterator<SeederInfo> iterator = sources.iterator();
        while (nRemainingBlocks(fileDescr) > 0 && iterator.hasNext() && active) {
            final SeederInfo seed = iterator.next();
            final List<Integer> absentBlocks = absentBlocks(fileDescr);
            parts.retainAll(absentBlocks);

            getPartsFrom(fileDescr, seed, parts);
        }

    }

    private Map<Integer, FileHolder> files() {
        return fileManager.getFiles();
    }

    private Map<String, FileStatus> status() {
        return fileManager.getStatus();
    }

    public boolean isDownloading(int fileId) {
        return files().containsKey(fileId) &&
                status().getOrDefault(files().get(fileId).getName(), NOT_READY) == NOT_READY;
    }

    public void startDownloading(FileDescr fileDescr) throws IOException {
        final String fileName = fileDescr.getName();
        final FileHolder fileHolder = FileHolder.leechedHolder(fileDescr.getId()
                , fileName
                , fileDescr.getSize()
                , fileManager.newFile(fileName));
//        downloadingFiles.put(fileDescr.getId(), fileHolder);
//        status.put(fileDescr.getName(), NOT_READY);
        files().put(fileDescr.getId(), fileHolder);
        status().put(fileDescr.getName(), NOT_READY);

    }

    public FileHolder getDownloadingFile(int fileId) {
        return files().get(fileId);
    }

    public void setStatus(String fileName, FileStatus newStatus) {
        status().put(fileName, newStatus);
    }

    public FileStatus getStatus(String fileName) {
        return status().getOrDefault(fileName, NOT_READY);
    }
}
