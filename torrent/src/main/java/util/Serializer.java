package util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;

public class Serializer {

    public static void serialize(Serializable obj, Path path) throws IOException {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            throw new IOException(e.getMessage() + "\n Can't open serialized filesystem \n");
        }
    }
}
