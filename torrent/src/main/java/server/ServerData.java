package server;

import client.FileDescr;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// TODO think about constructor
@RequiredArgsConstructor
@ToString
public class ServerData {

    // TODO consider using arraylist instead
    // TODO remove getter
    @Getter
    private final Map<Integer, FileDescr> filesById = new ConcurrentHashMap<>();
    private final Map<Integer, Set<SeederInfo>> seedersByFile = new ConcurrentHashMap<>();
    private final Map<Pair<Integer, Integer>, List<SeederInfo>> seederByFilePart = new HashMap<>();

    private final AtomicInteger newFileId = new AtomicInteger(0);

    public int upload(String name, long size) {
        final int newId = newFileId.getAndIncrement();
        final FileDescr fileDescr = new FileDescr(newId, name, (int) size);
        filesById.put(newId, fileDescr);
        return newId;
    }

    public void addSeeder(int fileId, final SeederInfo seederInfo) {
        System.out.println("Adding seeder");
        System.out.printf("fileId = %d\n", fileId);
        System.out.println(seederInfo);
        if (!seedersByFile.containsKey(fileId)) {
            seedersByFile.put(fileId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        }
        seedersByFile.get(fileId).add(new SeederInfo(seederInfo));
    }

    // TODO refactor
    public Set<SeederInfo> getSeeders(int fileId) {
        if (!seedersByFile.containsKey(fileId)) {
            return Collections.emptySet();
        }

        seedersByFile.get(fileId).removeIf(si ->!si.isValid());
        if (!seedersByFile.containsKey(fileId)) {
            return Collections.emptySet();
        }
        return seedersByFile.get(fileId);
    }

    public Collection<FileDescr> allFiles() {
        return filesById.values();
    }

}
