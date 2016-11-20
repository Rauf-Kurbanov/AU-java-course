package client;

import lombok.Data;

@Data
public class FileDescr {
    private final int id;
    private final String name;
    private final long size;
}
