package client;

import lombok.RequiredArgsConstructor;
import protocol.TorrentProtocol;
import server.SeederInfo;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static client.FileStatus.NOT_READY;
import static client.FileStatus.READY;
import static com.google.common.base.Preconditions.checkArgument;

@RequiredArgsConstructor
public class Leecher implements Serializable {

    private final FileManager fileManager;
    private final ExecutorService leecherExecutor = Executors.newSingleThreadExecutor();
    private final TorrentProtocol protocol = TorrentProtocol.INSTANCE;
    private volatile boolean active = true;

    private List<Integer> absentBlocks(FileDescr fileDescr) {
        final FileHolder fileHolder = fileManager.getFileHolder(fileDescr.getId());
        final int nParts = fileHolder.getNParts();
        final List<Integer> allParts = IntStream.range(0, nParts).boxed().collect(Collectors.toList());
        allParts.removeAll(fileHolder.getParts());
        return allParts;
    }

    private int nRemainingBlocks(FileDescr fileDescr) {
        final FileHolder fileHolder = fileManager.getFileHolder(fileDescr.getId());
        return fileHolder.getNParts() - fileHolder.getAcquiredParts();
    }

    public void getPartsFrom(FileDescr fileDescr, SeederInfo seederInfo) throws IOException {
        final Socket p2pSocket;
        try {
            p2pSocket = new Socket(seederInfo.getInetAddress(), seederInfo.getPort());
            final DataInputStream seederIn = new DataInputStream(p2pSocket.getInputStream());
            final DataOutputStream seederOut = new DataOutputStream(p2pSocket.getOutputStream());
            final int fileId = fileDescr.getId();

            if (!fileManager.isDownloading(fileId)) {
                fileManager.startDownloading(fileDescr);
            }

            List<Integer> parts = protocol.requestStat(seederIn, seederOut, fileId);
            final List<Integer> absentBlocks = absentBlocks(fileDescr);
            parts.retainAll(absentBlocks);

            System.out.println("PARTS:");
            parts.forEach(System.out::println);
            final FileHolder fileHolder = fileManager.getDownloadingFile(fileId);
            for (int partId : parts) {
                final byte[] content = protocol.requestGet(seederIn, seederOut, fileId, partId);
                final FileStatus status = fileHolder.readPart(partId, new ByteArrayInputStream(content));
            }

            seederIn.close();
            seederOut.close();
            p2pSocket.close();
        } catch (IOException ignored) {
        }
    }

    public void getPartsFrom(FileDescr fileDescr, Collection<SeederInfo> sources) throws IOException {
        leecherExecutor.execute(() -> {
            Iterator<SeederInfo> iterator = sources.iterator();
            while (nRemainingBlocks(fileDescr) > 0 && iterator.hasNext() && active) {
                final SeederInfo seed = iterator.next();
                try {
                    getPartsFrom(fileDescr, seed);
                } catch (IOException ignored) {
                }
            }
            final FileHolder fh = fileManager.getFileHolder(fileDescr.getId());
            if (fh.getAcquiredParts() == fh.getNParts()) {
                fileManager.setReady(fileDescr.getName());
            }
        });
    }
}
