package kkrasilnikovv.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import kkrasilnikovv.postprocessor.Controller;
import kkrasilnikovv.preprocessor.model.DataFile;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleDoublePropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;
import kkrasilnikovv.processor.CalculationFile;
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
    @Getter
    private static File calculationFile;
    private static Controller controller;
    private final static Gson gson = new GsonBuilder()
            .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
            .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
            .registerTypeAdapter(SimpleDoubleProperty.class, new SimpleDoublePropertyAdapter())
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
    public static void setTitle(String title){
        showsScene.setTitle(title);
    }
    public static void main(String[] args) {
        launch(args);
    }

    public static void showScene(Scene scene) {
        showsScene.setScene(scene);
    }

    public static void showMainScene() {
        showsScene.setScene(mainScene);
        showsScene.setTitle("САПР");
    }

    public static DataFile convertFileToData(File file, boolean isMainFile) {
        DataFile dataFile = null;
        if (Objects.nonNull(file)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                dataFile = gson.fromJson(reader, DataFile.class);

                if (Objects.isNull(dataFile) || dataFile.isEmpty()) {
                    showAlert("Получен файл с неверной структурой или с отсутствующими значениями.");
                }else {
                    if (isMainFile) {
                        Main.setDataFile(file);
                    }
                }
            } catch (FileNotFoundException e) {
                showAlert("Файл не найден.");
            } catch (JsonSyntaxException ex) {
                showAlert("Ошибка синтаксиса JSON в файле.");
            }
        }
        return dataFile;
    }
    public static CalculationFile convertFileToDataCalculation(File file) {
        CalculationFile calculationFile = null;
        if (Objects.nonNull(file)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                calculationFile = gson.fromJson(reader, CalculationFile.class);
                if (Objects.isNull(calculationFile) || calculationFile.isEmpty()) {
                    showAlert("Получен файл с неверной структурой или с отсутствующими значениями.");
                }else {
                    Main.setCalculationFile(file);
                }
            } catch (FileNotFoundException e) {
                showAlert("Файл не найден.");
            } catch (JsonSyntaxException ex) {
                showAlert("Ошибка синтаксиса JSON в файле.");
            }
        }
        return calculationFile;
    }

    public static void setCalculationFile(File calculationFile) {
        Main.calculationFile = calculationFile;
    }

    private static void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}