package protocol;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FtpFile {

    public final String path;
    public final boolean isDir;
}