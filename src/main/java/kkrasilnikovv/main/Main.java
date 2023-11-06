package kkrasilnikovv.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import kkrasilnikovv.preprocessor.model.DataFile;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.Objects;

public class Main extends Application {
    private static Stage showsScene;
    private static Scene mainScene;
    @Setter
    @Getter
    private static File dataFile;
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
            .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
            .create();

    @Override
    public void start(Stage primaryStage) throws IOException {
        showsScene = primaryStage;
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("main.fxml"));
        showsScene.setTitle("САПР");
        mainScene = new Scene(root, 800, 600);
        showsScene.setScene(mainScene);
        showsScene.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void showScene(Scene scene) {
        showsScene.setScene(scene);
    }

    public static void showMainScene() {
        showsScene.setScene(mainScene);
    }

    public static DataFile convertFileToData(File file, boolean isMainFile) {
        DataFile dataFile = new DataFile();
        if (Objects.nonNull(file)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                dataFile = gson.fromJson(reader, DataFile.class);
                if (isMainFile) {
                    Main.setDataFile(file);
                }
            } catch (FileNotFoundException e) {
                showAlert("Ошибка", "Файл не найден.", Alert.AlertType.ERROR);
            } catch (JsonSyntaxException ex) {
                showAlert("Ошибка", "Ошибка синтаксиса JSON в файле.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Информация", "Файл не найден.", Alert.AlertType.INFORMATION);
        }
        return dataFile;
    }

    private static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}