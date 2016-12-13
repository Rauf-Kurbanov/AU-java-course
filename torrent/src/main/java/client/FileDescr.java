package client;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FileDescr {
    private final int id;
    private final String name;
    private final int size;
}
