package kkrasilnikovv.preprocessor.controller;

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
    private double scale = 1.0;
    private double translateX = 0.0;
    private double translateY = 0.0;
    private boolean isSupportOnRight, isSupportOnLeft;

    public void initialize() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
        loadFromFile();
        setupMouseHandlers();
        setupZoomHandler();
        draw();
    }

    public void draw() {
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

        for (BeamData beam : beamList) {
            double x1 = centerX + beam.getStartPoint() * scale;
            double x2 = centerX + beam.getEndPoint() * scale;
            double y = centerY - beam.getWidth() * scale / 2;
            double height = beam.getWidth() * scale;
            gc.strokeRect(x1, y, x2 - x1, height); // Рисуем контур прямоугольника

//            // Рисуем изображение перед первым прямоугольником
//            if (beam == beamList.get(0)) {
//                Image image = new Image("support.png", 2, 2, true, true);
//                double imageX = x1 - image.getWidth(); // Изображение соприкасается с левой стороной прямоугольника
//                gc.drawImage(image, imageX, y, image.getWidth(), image.getHeight());
//            }
//
//            // Рисуем изображение после последнего прямоугольника
//            if (beam == beamList.get(beamList.size() - 1)) {
//                Image image = new Image("support.png", 200, 200, true, true);
//                gc.drawImage(image, x2, y, image.getWidth(), image.getHeight());
//            }
            if (!beamList.isEmpty()) {
                BeamData firstBeam = beamList.get(0);
                BeamData lastBeam = beamList.get(beamList.size() - 1);

                Image image = new Image("support.png");
                double x11 = centerX + firstBeam.getStartPoint() * scale;
                double x22 = centerX + lastBeam.getEndPoint() * scale;
                double yy = centerY - firstBeam.getWidth() * scale / 2;
                double heightt = firstBeam.getWidth() * scale;

                // Рисуем изображение перед первым прямоугольником
                gc.drawImage(image, x11 - image.getWidth(), yy, image.getWidth(), heightt);

                // Рисуем изображение после последнего прямоугольника
                gc.drawImage(image, x22, yy, image.getWidth(), heightt);
            }
        }
    }

    private void loadFromFile() {
        pointList.clear();
        beamList.clear();
        SavingFile savingFile = Main.getDataGSONFile();
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
