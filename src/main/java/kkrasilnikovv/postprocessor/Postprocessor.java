package kkrasilnikovv.postprocessor;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.util.Objects;

@Getter
public class Postprocessor {
    private final Scene mainScene;
    @Getter(AccessLevel.NONE)
    private static Postprocessor postprocessor;

    private Postprocessor() throws IOException {
        Parent main = FXMLLoader.load(ClassLoader.getSystemResource("postprocessor.fxml"));
        mainScene = new Scene(main, 800, 600);

    }

    public static Postprocessor getInstance() {
        if (Objects.isNull(postprocessor)) {
            try {
                postprocessor = new Postprocessor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return postprocessor;
    }
}
