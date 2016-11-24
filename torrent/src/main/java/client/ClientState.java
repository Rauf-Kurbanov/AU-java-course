package client;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClientState {

    private final FileManager fileManager;

    // TODO
    public FileHolder getFileInfo(int fileID) {
        return fileManager.getDownloadedFile(fileID);
    }
}
