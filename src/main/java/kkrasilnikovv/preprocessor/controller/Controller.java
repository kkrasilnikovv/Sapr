package kkrasilnikovv.preprocessor.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.stage.Stage;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;

import java.io.IOException;


public class Controller {
    public Button dataButton, drawButton, backButton;

    public void dataEvent() {
        Main.showScene(PreProcessor.getInstance().getDataScene());
    }

    public void drawEvent(ActionEvent event) {
        // Создайте объект FXMLLoader
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("preprocessor-draw.fxml"));

        // Загрузите FXML-файл и получите корневой узел (обычно это Pane или AnchorPane)
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Получите экземпляр контроллера DrawController
        DrawController drawController = loader.getController();

        // Переключитесь на экран с DrawController
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // event - событие, например, нажатие кнопки
        stage.setScene(scene);

        // Вызовите метод для отрисовки
        drawController.draw();
        Main.showScene(PreProcessor.getInstance().getDrawScene());
    }

    public void backEvent() {
        Main.showMainScene();
    }
}
