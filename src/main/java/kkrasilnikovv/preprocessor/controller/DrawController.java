package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.SavingFile;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrawController {
    @FXML
    public Canvas coordinateCanvas;
    @FXML
    public Button backButton;

    private List<PointData> pointList;
    private List<BeamData> beamList;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isDragging = false;
    private double scale = 50.0;
    private double translateX = 0.0;
    private double translateY = 0.0;
    private boolean isSupportOnRight, isSupportOnLeft;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
            .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
            .create();

    public void initialize() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
        setupMouseHandlers();
        setupZoomHandler();
        draw();
    }

    public void draw() {
        loadFromFile();
        GraphicsContext gc = coordinateCanvas.getGraphicsContext2D();
        double coordinateCanvasWidth = coordinateCanvas.getWidth();
        double coordinateCanvasHeight = coordinateCanvas.getHeight();

        // Очищаем Canvas
        gc.clearRect(0, 0, coordinateCanvasWidth, coordinateCanvasHeight);

        // Рисуем координатные оси с учетом масштаба и сдвига
        double centerX = coordinateCanvasWidth / 2 + translateX;
        double centerY = coordinateCanvasHeight / 2 + translateY;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeLine(0, centerY, coordinateCanvasWidth, centerY); // Горизонтальная ось

        gc.setStroke(Color.RED);
        // Позиция оси X после учета сдвига
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        for (BeamData beam : beamList) {
            double x1 = centerX + beam.getStartPoint() * scale;
            double x2 = centerX + beam.getEndPoint() * scale;
            double y = centerY - beam.getWidth() * scale / 2;
            double height = beam.getWidth() * scale;
            gc.strokeRect(x1, y, x2 - x1, height); // Рисуем контур прямоугольника

// Рисуем изображение перед первым прямоугольником
            if (isSupportOnLeft && beam.equals(beamList.get(0))) {
                Image image = new Image("support_left.PNG");
                gc.drawImage(image, x1 - imageWidth, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
            }

            // Рисуем изображение после последнего прямоугольника
            if (isSupportOnRight && beam.equals(beamList.get(beamList.size() - 1))) {
                Image image = new Image("support_right.PNG");
                gc.drawImage(image, x2 + 1, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
            }
        }
    }

    private void loadFromFile() {
        pointList.clear();
        beamList.clear();
        SavingFile savingFile = Main.getDataGSONFile();
        if (Objects.isNull(savingFile)) {
            try {
                FileReader selectedFile = new FileReader("data.json");
                BufferedReader reader = new BufferedReader(selectedFile);
                savingFile = gson.fromJson(reader, SavingFile.class);
            } catch (FileNotFoundException e) {
                System.out.println("Стандартный файл data.json не найден.");
            }
        }
        if (Objects.nonNull(savingFile)) {
            pointList = savingFile.getPointList();
            beamList = savingFile.getBeamList();
            isSupportOnLeft = savingFile.isSupportOnLeft();
            isSupportOnRight = savingFile.isSupportOnRight();
        }

    }

    private void setupMouseHandlers() {
        coordinateCanvas.setOnMousePressed(this::handleMousePressed);
        coordinateCanvas.setOnMouseDragged(this::handleMouseDragged);
        coordinateCanvas.setOnMouseReleased(this::handleMouseReleased);
    }

    private void handleMousePressed(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            isDragging = true;
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();
        }
    }

    private void handleMouseDragged(MouseEvent event) {
        if (isDragging) {
            double deltaX = event.getSceneX() - lastMouseX;
            double deltaY = event.getSceneY() - lastMouseY;
            lastMouseX = event.getSceneX();
            lastMouseY = event.getSceneY();

            translateX += deltaX;
            translateY += deltaY;
            draw();
        }
    }

    private void handleMouseReleased(MouseEvent event) {
        if (event.isPrimaryButtonDown()) {
            isDragging = false;
        }
    }

    private void setupZoomHandler() {
        coordinateCanvas.setOnScroll(this::handleScroll);
    }

    private void handleScroll(ScrollEvent event) {
        double deltaY = event.getDeltaY();

        if (deltaY < 0) {
            scale /= 1.1;
        } else {
            scale *= 1.1;
        }

        draw();

        event.consume();
    }

    public void backup() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }
}
