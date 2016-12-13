package ui;

import client.FileDescr;
import client.FileStatus;
import client.TorrentClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;

//import java.io.File;
import java.io.IOException;
import java.util.List;

public class Controller {

//    static final String dataHome = "/home/esengie/rauf/AU-java-course/torrent/test_data";

    private ObservableList<FileDescr> fileData = FXCollections.observableArrayList();

    @FXML
    private TableView<FileDescr> myTableView;
    @FXML
    private TableColumn<FileDescr, String> fileNameColumn;
    @FXML
    private TableColumn<FileDescr, Integer> fileIdColumn;
    @FXML
    private TableColumn<FileDescr, Integer> sizeColumn;
    @FXML
    private TableColumn<FileDescr, String> statusColumn;

    @FXML
    private Button updateButton;
    @FXML
    private Button uploadButton;

    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private TextField fileNameField1;

    @Setter
    private TorrentClient client;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void run() {

        stage.setOnCloseRequest(e -> {
            try {
                client.pause();
            } catch (IOException e1) {
                throw new RuntimeException(e1.getMessage());
            }
            Platform.exit();
            System.exit(0);
        });

        fileNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        fileIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()));
        sizeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSize()));
        statusColumn.setCellValueFactory(cellData ->
                client.getStatus(cellData.getValue().getName()) ==
                        FileStatus.NOT_READY ? new SimpleStringProperty("Peering") : new SimpleStringProperty("Seeding"));

        myTableView.setRowFactory(tv -> {
            TableRow<FileDescr> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    FileDescr rowData = row.getItem();
                    System.out.println(rowData);
                    try {
                        client.pullSources(rowData.getId());
                        client.getFile(rowData);
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            });
            return row;
        });

        updateButton.setOnAction(event -> {
            try {
                List<FileDescr> fds = client.list();
                fileData.clear();
                fileData.addAll(fds);

                myTableView.setItems(fileData);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        uploadButton.setOnAction(event -> {
            try {
                client.upload(fileNameField1.getText());
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        });

    }


}
