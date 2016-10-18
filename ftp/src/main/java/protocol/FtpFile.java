package protocol;

public class FtpFile {

    public final String path;
    public final boolean isDir;

    FtpFile(String path, boolean isDir){
        this.path = path;
        this.isDir = isDir;
    }
}