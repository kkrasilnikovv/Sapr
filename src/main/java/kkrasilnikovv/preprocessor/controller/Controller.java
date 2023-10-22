package kkrasilnikovv.preprocessor.controller;

import javafx.scene.control.Button;

import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;


public class Controller {

    public Button dataButton, drawButton, backButton, loadButton;

    public void dataEvent() {
        Main.showScene(PreProcessor.getInstance().getDataScene());
    }

    public void drawEvent() {
        Main.showScene(PreProcessor.getInstance().getDrawScene());
    }

    public void backEvent() {
        Main.showMainScene();
    }


}
