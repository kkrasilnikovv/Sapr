package kkrasilnikovv.preprocessor.controller;

import javafx.scene.control.Button;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.Renderer;


public class Controller {
    private final Renderer renderer = new Renderer();
    public Button dataButton, drawButton, backButton;

    public void dataEvent() {
        Main.showScene(PreProcessor.getInstance().getDataScene());
    }

    public void drawEvent() {
        renderer.draw();
    }

    public void backEvent() {
        Main.showMainScene();
    }
}
