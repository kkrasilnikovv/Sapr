package kkrasilnikovv.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.model.SavingFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Controller {
    @FXML
    public Button processorButton, preProcessorButton, postProcessorButton, loadButton;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
            .create();

    @FXML
    public void preProcessorEvent() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }

    @FXML
    public void processorEvent() {
    }

    @FXML
    public void postProcessorEvent() {
    }

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*data.json"));

        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (selectedFile != null) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                SavingFile savingFile = gson.fromJson(reader, SavingFile.class);
                Main.setDataFile(selectedFile);
                Main.setDataGSONFile(savingFile);
            } catch (FileNotFoundException e) {
                showAlert("Ошибка", "Файл не найден.", Alert.AlertType.ERROR);
            } catch (JsonSyntaxException ex) {
                showAlert("Ошибка", "Ошибка синтаксиса JSON в файле.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Информация", "Файл не выбран.", Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
