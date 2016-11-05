package server;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ServerState {

    private Map<Integer, FileInfo> filesById;
    private Map<Integer, List<Seeder>> seedersByFile;
    private Map<Seeder, FileInfo> filesBySeeder;

    public int upload(String name, long size) {
        return 0;
    }

    public boolean update(Short clientPort, int fileId) {
        return false;
    }

}
