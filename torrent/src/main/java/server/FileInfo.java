package server;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class FileInfo {

    public FileInfo(int id, List<Integer> parts, String name, long size) {
        this.id = id;
        this.parts = parts;
        this.name = name;
        this.size = size;
    }

    public static final int PART_SIZE = 10_000_000;
    private int id;
    @Getter
    @Setter
    private List<Integer> parts;
    @Getter
    private String name;
    @Getter
    private long size;
}
