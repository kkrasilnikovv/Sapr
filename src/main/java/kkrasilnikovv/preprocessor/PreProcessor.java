package kkrasilnikovv.preprocessor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

@Getter
public class PreProcessor {
    private final Scene mainScene;
    private final Scene dataScene;
    private final Scene drawScene;
    @Getter(AccessLevel.NONE)
    private static PreProcessor preProcessor;


    private PreProcessor() throws IOException {
        Parent main = FXMLLoader.load(ClassLoader.getSystemResource("preprocessor-main.fxml"));
        mainScene = new Scene(main, 800, 600);
        Parent data = FXMLLoader.load(ClassLoader.getSystemResource("preprocessor-data.fxml"));
        dataScene = new Scene(data, 800, 600);
        Parent draw = FXMLLoader.load(ClassLoader.getSystemResource("preprocessor-draw.fxml"));
        drawScene = new Scene(draw, 800, 600);
    }

    public static PreProcessor getInstance() {
        if (Objects.isNull(preProcessor)) {
            try {
                preProcessor = new PreProcessor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return preProcessor;
    }
}
