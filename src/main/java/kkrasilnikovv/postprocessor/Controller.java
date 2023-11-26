package kkrasilnikovv.postprocessor;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.processor.CalculationFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Controller {

    private final Gson gson = new Gson();
    public Button loadButton, calculateButton, backButton;

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("JSON Files", "*calculation.json"));
        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        loadFromFile(convertFileToData(selectedFile));
    }

    public void showEvent(ActionEvent event) {
    }

    public void backEvent(MouseEvent mouseEvent) {
        Main.showMainScene();
    }

    private void loadFromFile(CalculationFile calculationFile) {

    }

    public CalculationFile convertFileToData(File file) {
        CalculationFile calculationFile = new CalculationFile();
        if (Objects.nonNull(file)) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                calculationFile = gson.fromJson(reader, CalculationFile.class);
            } catch (FileNotFoundException e) {
                showAlert("Файл не найден.");
            } catch (JsonSyntaxException ex) {
                showAlert("Ошибка синтаксиса JSON в файле.");
            }
        }
        return calculationFile;
    }

    private static void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
