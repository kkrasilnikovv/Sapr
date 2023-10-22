package kkrasilnikovv.preprocessor.controller;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;

public class DrawController {
    @FXML
    public Canvas coordinateCanvas;
    public Button backButton;

    public void initialize() {
        drawCoordinateSystem();
    }

    private void drawCoordinateSystem() {
        GraphicsContext gc = coordinateCanvas.getGraphicsContext2D();

        // Очистка Canvas
        gc.clearRect(0, 0, coordinateCanvas.getWidth(), coordinateCanvas.getHeight());

        // Отрисовка координатных осей
        double centerX = coordinateCanvas.getWidth() / 2;
        double centerY = coordinateCanvas.getHeight() / 2;
        double axisLength = Math.min(centerX, centerY) - 10;

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeLine(centerX, 0, centerX, coordinateCanvas.getHeight());
        gc.strokeLine(0, centerY, coordinateCanvas.getWidth(), centerY);

        // Отрисовка меток на осях
        int step = 20; // Шаг между метками
        for (int x = 0; x < centerX; x += step) {
            gc.strokeLine(centerX + x, centerY - 5, centerX + x, centerY + 5);
            gc.strokeLine(centerX - x, centerY - 5, centerX - x, centerY + 5);
        }
        for (int y = 0; y < centerY; y += step) {
            gc.strokeLine(centerX - 5, centerY + y, centerX + 5, centerY + y);
            gc.strokeLine(centerX - 5, centerY - y, centerX + 5, centerY - y);
        }
    }

    public void backup() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }
}
