package kkrasilnikovv.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import kkrasilnikovv.preprocessor.PreProcessor;


public class Controller {
    @FXML
    public Button processorButton, preProcessorButton, postProcessorButton;

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
}
