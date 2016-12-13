package ui;

import client.TorrentClient;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    private static final int portNumber = ServerRunner.portNumber;
    private static final String dataHome = ServerRunner.dataHome;

    @Override
    public void start(Stage primaryStage) throws Exception{

        final FXMLLoader loader = new FXMLLoader(
                getClass().getClassLoader().getResource(
                        "scene.fxml"));

        final Parent root = loader.load();

        final Controller controller = loader.getController();
        controller.setStage(primaryStage);

        final File fsB = new File(dataHome, "fsB");
        TorrentClient clientB;
        if (TorrentClient.canResume(fsB.toPath())) {
            clientB = TorrentClient.resume("127.0.0.1", portNumber, fsB.toPath());
        } else {
            clientB = new TorrentClient("127.0.0.1", portNumber, fsB.toPath());
        }
        controller.setClient(clientB);

        primaryStage.setTitle("Torrent client");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        controller.run();
    }

    public static void main(String[] args) throws IOException {
        launch(args);
    }
}
