package kkrasilnikovv.processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.model.DataFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Controller {
    private final Gson gson = new GsonBuilder().create();
    public Button loadButton, calculateButton, backButton;
    private CalculationFile calculationFile;
    private DataFile dataFile;

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*data.json"));
        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        loadFromFile(Main.convertFileToData(selectedFile, false));
    }

    private void loadFromFile(DataFile dataFile) {
        this.dataFile = dataFile;
    }

    public void calculateEvent() {
        DataFile selectedFile = null;
        if (Objects.nonNull(dataFile) && !dataFile.isEmpty()) {
            selectedFile = dataFile;
        } else {
            DataFile mainFile = Main.convertFileToData(Main.getDataFile(), false);
            if (Objects.nonNull(mainFile) && !mainFile.isEmpty()) {
                selectedFile = mainFile;
            }
        }
        if (Objects.nonNull(selectedFile) && !selectedFile.isEmpty()) {
            saveCalculation();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не найден файл с исходными данными. Выберите файл при помощи \"Загрузить данные из файла\".");
            alert.showAndWait();
        }
    }

    private void saveCalculation() {
        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        alertInfo.setHeaderText("Выберите папку для сохранения файла.");
        alertInfo.showAndWait();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Выберите папку для сохранения файла");

        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            // Отображаем диалоговое окно для ввода имени файла
            TextInputDialog dialog = new TextInputDialog("calculation");
            dialog.setTitle("Имя файла");
            dialog.setHeaderText("Введите имя файла:");
            dialog.setContentText("Имя файла:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String fileName = result.get();
                File file = new File(selectedDirectory, fileName + ".json");
                writeCalculationToFile(file);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Данные не будут сохранены");
                alert.setHeaderText("Имя файла не выбрано.");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Директория не выбрана.");
            alert.showAndWait();
        }
    }

    private void writeCalculationToFile(File file) {
        Alert alert;
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(gson.toJson(calculationFile));
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Данные успешно сохранены");
            alert.setHeaderText("Сохранено в файл: " + file.getAbsolutePath());
            alert.showAndWait();
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Невозможно сохранить в файл:" + file.getName());
            alert.showAndWait();
        }
    }

    private void calculateNormalVoltage() {

    }

    private void calculateLongitudinalStrong() {

    }

    private void calculateMoving() {

    }

    public void backEvent() {
        Main.showMainScene();
    }
}
