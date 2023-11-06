package kkrasilnikovv.preprocessor;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.DataFile;
import kkrasilnikovv.preprocessor.model.SectionType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Renderer {
    private double orgSceneX, orgSceneY,orgTranslateX, orgTranslateY;
    public Canvas coordinateCanvas;
    private List<PointData> pointList;
    private List<BeamData> beamList;
    private boolean isSupportOnRight, isSupportOnLeft;

    public Renderer() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
    }

    public void draw() {
        loadFromFile();
        coordinateCanvas = new Canvas(1920,1080);
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

        //Рисуем тип сечения
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
            gc.fillText("Тип сечения", x + imageTypeWidth / 2 / 2, y - 5);
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
        DataFile dataFile = null;
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            dataFile = Main.convertFileToData(file,false);
        }

        if (Objects.nonNull(dataFile)) {
            pointList = dataFile.getPointList();
            beamList = dataFile.getBeamList();
            isSupportOnLeft = dataFile.isSupportOnLeft();
            isSupportOnRight = dataFile.isSupportOnRight();
        }

    }

    private void showImagePreview() {
        try {
            File file = new File("image.png");
            Image image = new Image(file.toURI().toString());
            Stage stage = new Stage();
            // Создаем элемент ImageView и устанавливаем в него изображение
            ImageView imageView = new ImageView(image);


            imageView.setOnMousePressed((MouseEvent event) -> {
                orgSceneX = event.getSceneX();
                orgSceneY = event.getSceneY();
                orgTranslateX = imageView.getTranslateX();
                orgTranslateY = imageView.getTranslateY();
            });

            imageView.setOnMouseDragged((MouseEvent event) -> {
                double offsetX = event.getSceneX() - orgSceneX;
                double offsetY = event.getSceneY() - orgSceneY;
                double newTranslateX = orgTranslateX + offsetX;
                double newTranslateY = orgTranslateY + offsetY;

                imageView.setTranslateX(newTranslateX);
                imageView.setTranslateY(newTranslateY);
            });

            imageView.setOnScroll(event -> {
                double deltaY = event.getDeltaY();
                double scale = imageView.getScaleX();
                if (deltaY > 0) {
                    scale *= 1.1; // Увеличить масштаб при вращении колесика вперед
                } else {
                    scale *= 0.9; // Уменьшить масштаб при вращении колесика назад
                }
                imageView.setScaleX(scale);
                imageView.setScaleY(scale);
            });

            VBox vbox = new VBox(imageView);
            Scene scene = new Scene(vbox, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Image Preview");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
