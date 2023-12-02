package kkrasilnikovv.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import kkrasilnikovv.postprocessor.Postprocessor;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.processor.Processor;

import java.io.File;
import java.util.Objects;


public class Controller {
    @FXML
    public Button processorButton, preProcessorButton, postProcessorButton;

    @FXML
    public void preProcessorEvent() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            Main.setTitle(file.getAbsolutePath());
        }
    }

    @FXML
    public void processorEvent() {
        Main.showScene(Processor.getInstance().getMainScene());
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            Main.setTitle(file.getAbsolutePath());
        }
    }

    @FXML
    public void postProcessorEvent() {
        Main.showScene(Postprocessor.getInstance().getMainScene());
        File file = Main.getCalculationFile();
        if (Objects.nonNull(file)) {
            Main.setTitle(file.getAbsolutePath());
        }
    }
}
