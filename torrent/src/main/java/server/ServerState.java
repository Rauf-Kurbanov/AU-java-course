package server;

import client.Seeder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// TODO think about constructor
@RequiredArgsConstructor
@ToString
public class ServerState {

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private class SeederInfo {

        public static final long LIFETIME = 5 * 60 * 1000;
        @Getter
        private final Seeder seeder;
        private final Date updateTime = new Date();

        public boolean isValid() {
            final long diff = (new Date()).getTime() - updateTime.getTime();
            return (diff <= LIFETIME);
        }
    }

    // TODO consider using arraylist instead
    // TODO remove getter
    @Getter
    private final Map<Integer, FileInfo> filesById = new ConcurrentHashMap<>();
    private final Map<Integer, Set<SeederInfo>> seedersByFile = new ConcurrentHashMap<>();
//    private final Map<Seeder, List<FileInfo>> filesBySeeder = new HashMap<>();
    private final Map<Pair<Integer, Integer>, List<Seeder>> seederByFilePart = new HashMap<>();

    private final AtomicInteger newFileId = new AtomicInteger(0);

    public int upload(String name, long size) {
//        System.out.printf("upload: name=%s size=%d\n", name, size);
        final int newId = newFileId.getAndIncrement();
        ArrayList<Integer> parts = new ArrayList<Integer>();
        // TODO rewrite prettier !!!
        int i = 0;
        for (i = 0; i < size / FileInfo.PART_SIZE; i++) {
            parts.add(i);
        }
        if (size % FileInfo.PART_SIZE > 0) {
            parts.add(i);
        }
        FileInfo fileInfo = new FileInfo(newId, parts, name, size);
        filesById.put(newId, fileInfo);
//        System.out.printf("newId = %s\n", newId);
        return newId;
    }

    public void addSeeder(int fileId, final Seeder seeder) {
        if (!seedersByFile.containsKey(fileId)) {
            seedersByFile.put(fileId, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        }
        seedersByFile.get(fileId).add(new SeederInfo(seeder));
    }

//    public List<Seeder> getSeeders(int fileId, int part) {
//        return seederByFilePart.get(new Pair<>(fileId, part));
//    }

    public Set<Seeder> getSeeders(int fileId) {
        seedersByFile.get(fileId).removeIf(si ->!si.isValid());
        return seedersByFile.get(fileId).stream()
                .map(SeederInfo::getSeeder)
                .collect(Collectors.toSet());
    }

//    public void setFiles(Seeder seeder, List<FileInfo> fileInfos) {
//        filesBySeeder.put(seeder, fileInfos);
//    }

//    public FileInfo getFileInfo(int fileId) {
//        return filesById.get(fileId);
//    }

    public Collection<FileInfo> allFiles() {
        ///
        System.out.println("allFiles");
        filesById.values().forEach(fi -> System.out.println(fi.toString()));
        ///
        return filesById.values();
    }

//    public boolean update(Short clientPort, int fileId) {
//        return false;
//    }

}
