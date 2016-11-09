package server;

import client.Seeder;
import lombok.RequiredArgsConstructor;
import util.Pair;

import java.util.*;

// TODO think about constructor
@RequiredArgsConstructor
public class ServerState {

    private final Map<Integer, FileInfo> filesById = new HashMap<>();
    private final Map<Integer, List<Seeder>> seedersByFile = new HashMap<>();
    private final Map<Seeder, List<FileInfo>> filesBySeeder = new HashMap<>();
    private final Map<Pair<Integer, Integer>, List<Seeder>> seederByFilePart = new HashMap<>();

    private int newFileId = 0;

    public int upload(String name, long size, Seeder seeder) {
        int newId = newFileId++;
        ArrayList<Integer> parts = new ArrayList<Integer>();
        int i = 0;
        for (i = 0; i < size / FileInfo.PART_SIZE; i++) {
            parts.add(i);
        }
        if (size % FileInfo.PART_SIZE > 0) {
            parts.add(i + 1);
        }
        FileInfo fileInfo = new FileInfo(newId, parts, name, size);
        filesById.put(newId, fileInfo);
        return 0;
    }

    public List<Seeder> getSeeders(int fileId, int part) {
        return seederByFilePart.get(new Pair<>(fileId, part));
    }

    public List<Seeder> getSeeders(int fileId) {
        return seedersByFile.get(fileId);
    }

    public void setFiles(Seeder seeder, List<FileInfo> fileInfos) {
        filesBySeeder.put(seeder, fileInfos);
    }

    public FileInfo getFileInfo(int fileId) {
        return filesById.get(fileId);
    }

    public Collection<FileInfo> allFiles() {
        return filesById.values();
    }

    public boolean update(Short clientPort, int fileId) {
        return false;
    }

}
