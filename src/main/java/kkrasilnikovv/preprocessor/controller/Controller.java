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

    public void drawEvent() {
        Main.showScene(PreProcessor.getInstance().getDrawHelloScene());
    }

    public void backEvent() {
        Main.showMainScene();
    }
}
