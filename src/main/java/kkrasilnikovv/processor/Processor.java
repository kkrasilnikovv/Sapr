package kkrasilnikovv.processor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import kkrasilnikovv.preprocessor.PreProcessor;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

@Getter
public class Processor {
    private final Scene mainScene;
    @Getter(AccessLevel.NONE)
    private static Processor processor;

    private Processor() throws IOException {
        Parent main = FXMLLoader.load(ClassLoader.getSystemResource("processor.fxml"));
        mainScene = new Scene(main, 800, 600);

    }

    public static Processor getInstance() {
        if (Objects.isNull(processor)) {
            try {
                processor = new Processor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return processor;
    }
}
