package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javafx.scene.paint.Color;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.SavingFile;
import kkrasilnikovv.preprocessor.model.SectionType;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrawController {
    @FXML
    private ImageView imagePreview;
    @FXML
    public Canvas coordinateCanvas;
    @FXML
    public Button backButton;

    private List<PointData> pointList;
    private List<BeamData> beamList;
    private boolean isSupportOnRight, isSupportOnLeft;
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
            .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
            .create();
    private double initialX = 0.0;
    private double initialY = 0.0;

    public void initialize() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
        draw();
        saveCanvasAsImage();
        showImagePreview();
        imagePreview.setOnScroll(event -> {
            double deltaY = event.getDeltaY();
            double zoomFactor = 1.1; // Фактор масштабирования

            if (deltaY < 0) {
                // Уменьшение изображения
                imagePreview.setScaleX(imagePreview.getScaleX() / zoomFactor);
                imagePreview.setScaleY(imagePreview.getScaleY() / zoomFactor);
            } else {
                // Увеличение изображения
                imagePreview.setScaleX(imagePreview.getScaleX() * zoomFactor);
                imagePreview.setScaleY(imagePreview.getScaleY() * zoomFactor);
            }

            event.consume();
        });
        imagePreview.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                initialX = event.getSceneX();
                initialY = event.getSceneY();
            }
        });

        imagePreview.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown()) {
                double offsetX = event.getSceneX() - initialX;
                double offsetY = event.getSceneY() - initialY;

                imagePreview.setTranslateX(imagePreview.getTranslateX() + offsetX);
                imagePreview.setTranslateY(imagePreview.getTranslateY() + offsetY);

                initialX = event.getSceneX();
                initialY = event.getSceneY();
            }
        });
    }

    public void draw() {
        loadFromFile();
        GraphicsContext gc = coordinateCanvas.getGraphicsContext2D();
        double coordinateCanvasWidth = coordinateCanvas.getWidth();
        double coordinateCanvasHeight = coordinateCanvas.getHeight();
        // Очищаем Canvas
        gc.clearRect(0, 0, coordinateCanvasWidth, coordinateCanvasHeight);

        // Рисуем координатные оси с учетом масштаба и сдвига
        double centerX = coordinateCanvasWidth / 2;
        double centerY = coordinateCanvasHeight / 2;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeLine(0, centerY, coordinateCanvasWidth, centerY); // Горизонтальная ось

        gc.setStroke(Color.BLACK);
        // Позиция оси X после учета сдвига
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        int scale = 50;
        double lastX = 0;
        for (BeamData beam : beamList) {
            double x1 = centerX + beam.getX1() * scale;
            double x2 = centerX + beam.getX2() * scale;
            double y = centerY - (double) (beam.getWidth() * scale) / 2;
            double height = beam.getWidth() * scale;
            gc.strokeRect(x1, y, x2 - x1, height); // Рисуем контур прямоугольника

            //Рисуем силы на стержнях
            int strong = beam.getStrongQ();
            if (strong > 0) {
                drawStrongQ(gc, new Image("/right_strong_Q.png"), x1, x2, y, height, strong);
            } else if (strong < 0) {
                drawStrongQ(gc, new Image("/left_strong_Q.png"), x1, x2, y, height, strong);
            }

            // Рисуем поддержку перед первым прямоугольником
            if (isSupportOnLeft && beam.equals(beamList.get(0))) {
                Image image = new Image("/support_left.PNG");
                gc.drawImage(image, x1 - imageWidth, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
            }

            // Рисуем поддержку после последнего прямоугольника
            if (isSupportOnRight && beam.equals(beamList.get(beamList.size() - 1))) {
                Image image = new Image("/support_right.PNG");
                gc.drawImage(image, x2 + 1, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
                lastX = x2 + 1 + imageWidth;
            }
            if (!isSupportOnRight && beam.equals(beamList.get(beamList.size() - 1))) {
                lastX = x2;
            }
            gc.fillText(beam.getX2() - beam.getX1() + "L", (x1 + x2) / 2 - 10, y + height + 12);
        }

        //Рисуем силы на точку
        for (PointData point : pointList) {
            int strong = point.getStrong();
            if (strong > 0) {
                drawStrongF(gc, new Image("/right_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
            } else if (strong < 0) {
                drawStrongF(gc, new Image("/left_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
            }
        }
        if (Objects.nonNull(beamList) && !beamList.isEmpty()) {
            String type = beamList.get(0).getSectionType();
            Image image = null;
            if (type.equals(SectionType.TRIANGLE.toString())) {
                image = new Image("/triangle.png");
            } else if (type.equals(SectionType.CIRCLE.toString())) {
                image = new Image("/circle.png");
            } else if (type.equals(SectionType.RECTANGLE.toString())) {
                image = new Image("/rectangle.png");
            }
            double x = lastX + 100;
            double y = centerY - 200;
            double imageTypeWidth = 150; // Фиксированная ширина изображения
            double imageTypeHeight = 150; // Фиксированная высота изображения
            gc.drawImage(image, x, y, imageTypeWidth, imageTypeHeight);
            gc.fillText("Тип сечения", x+imageTypeWidth/2/2, y-5);
        }
        saveCanvasAsImage();
        showImagePreview();
    }

    private void drawStrongF(GraphicsContext gc, Image image, double x, double y, int strong) {
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        gc.drawImage(image, x, y - imageHeight / 2, imageWidth, imageHeight);
        gc.fillText(strong + "F", x + imageWidth / 2, y - 15);
    }

    private void drawStrongQ(GraphicsContext gc, Image image, double x1, double x2, double y, double height, int strong) {
        double imageWidth = 20;
        double imageHeight = 20;
        double start = x1 - imageWidth;
        int max = (int) ((x2 - x1) / imageWidth);
        for (int i = 0; i < max; i++) {
            start += imageWidth;
            gc.drawImage(image, start, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
        }
        gc.fillText(strong + "q", (x1 + x2) / 2 - 10, y - 5);
    }

    private void saveCanvasAsImage() {
        // Создайте WritableImage из содержимого Canvas
        WritableImage writableImage = new WritableImage((int) coordinateCanvas.getWidth(),
                (int) coordinateCanvas.getHeight());
        coordinateCanvas.snapshot(null, writableImage);

        // Создайте файл для сохранения изображения
        File file = new File("image.png");

        try {
            // Преобразуйте WritableImage в BufferedImage (для формата PNG)
            BufferedImage bufferedImage = new BufferedImage((int) coordinateCanvas.getWidth(),
                    (int) coordinateCanvas.getHeight(), BufferedImage.TYPE_INT_ARGB);
            SwingFXUtils.fromFXImage(writableImage, bufferedImage);

            // Сохраните изображение в файл в формате PNG
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        pointList.clear();
        beamList.clear();
        SavingFile savingFile = null;
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            savingFile = Main.convertFileToData(file);
        }

        if (Objects.nonNull(savingFile)) {
            pointList = savingFile.getPointList();
            beamList = savingFile.getBeamList();
            isSupportOnLeft = savingFile.isSupportOnLeft();
            isSupportOnRight = savingFile.isSupportOnRight();
        }

    }

    private void showImagePreview() {
        try {
            File file = new File("image.png");
            Image image = new Image(file.toURI().toString());
            imagePreview.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void backup() {
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }
}
