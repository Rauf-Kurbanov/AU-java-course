package server;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
public class FileInfo {

    public FileInfo(int id, List<Integer> parts, String name, long size) {
        this.id = id;
        this.parts = parts;
        this.name = name;
        this.size = size;
    }

    public static final int PART_SIZE = 10_000_000;
    @Getter
    private int id;
    @Getter
    @Setter
    private List<Integer> parts;
    @Getter
    private String name;
    @Getter
    private long size;
}
