package kkrasilnikovv.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import kkrasilnikovv.postprocessor.Postprocessor;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.processor.Processor;


public class Controller {
    @FXML
    public Button processorButton, preProcessorButton, postProcessorButton;

    @FXML
    public void preProcessorEvent() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }

    @FXML
    public void processorEvent() {
        Main.showScene(Processor.getInstance().getMainScene());
    }

    @FXML
    public void postProcessorEvent() {
        Main.showScene(Postprocessor.getInstance().getMainScene());
    }
}
