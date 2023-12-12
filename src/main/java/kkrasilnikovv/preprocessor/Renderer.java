package kkrasilnikovv.preprocessor;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.model.Beam;
import kkrasilnikovv.preprocessor.model.Point;
import kkrasilnikovv.preprocessor.model.DataFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class Renderer {
    private double orgSceneX, orgSceneY, orgTranslateX, orgTranslateY;
    public Canvas coordinateCanvas;
    private List<Point> pointList;
    private List<Beam> beamList;
    private final int scale = 50;
    double centerX, centerY;
    private boolean isSupportOnRight, isSupportOnLeft;

    public Renderer() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
    }

    public void draw() {
        try {
            loadFromFile();
        } catch (Exception e) {
            return;
        }

        coordinateCanvas = new Canvas(5000, 1500);
        GraphicsContext gc = coordinateCanvas.getGraphicsContext2D();
        double coordinateCanvasWidth = coordinateCanvas.getWidth();
        double coordinateCanvasHeight = coordinateCanvas.getHeight();
        // Очищаем Canvas
        gc.clearRect(0, 0, coordinateCanvasWidth, coordinateCanvasHeight);

        // Рисуем координатные оси с учетом масштаба и сдвига
        centerX = coordinateCanvasWidth / 2;
        centerY = coordinateCanvasHeight / 2;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1.0);
        gc.strokeLine(0, centerY, coordinateCanvasWidth, centerY); // Горизонтальная ось

        gc.setStroke(Color.BLACK);
        // Позиция оси X после учета сдвига
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        List<Beam> newBeamList = assignCoefficients(beamList);
        for (Beam beam : newBeamList) {
            double x1 = centerX + beam.getX1() * scale;
            double x2 = centerX + beam.getX2() * scale;
            double y = centerY - (beam.getCoefficient() * scale) / 2;
            double height = beam.getCoefficient() * scale;
            gc.strokeRect(x1, y, x2 - x1, height); // Рисуем контур прямоугольника

            //Рисуем силы на стержнях
            int strong = beam.getStrongQ();
            double square = beam.getSquare();
            if (strong > 0) {
                drawStrongQ(gc, new Image("/strong_Q.png"), x1, x2, y, height, strong, square);
            } else if (strong < 0) {
                drawStrongQ(gc, new Image("/strong_Q.png"), x1, x2, y, height, strong, square);
            } else {
                gc.fillText(square + "A", (x1 + x2) / 2 - 10, y - 5);
            }

            // Рисуем поддержку перед первым прямоугольником
            if (isSupportOnLeft && beam.equals(newBeamList.get(0))) {
                Image image = new Image("/support_left.PNG");
                gc.drawImage(image, x1 - imageWidth, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
            }

            // Рисуем поддержку после последнего прямоугольника
            if (isSupportOnRight && beam.equals(newBeamList.get(newBeamList.size() - 1))) {
                Image image = new Image("/support_right.PNG");
                gc.drawImage(image, x2 + 1, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
            }
            gc.fillText(beam.getX2() - beam.getX1() + "L", (x1 + x2) / 2 - 10, y + height + 12);
        }

        //Рисуем силы на точку
        for (Point point : pointList) {
            boolean isFirst = pointList.indexOf(point) == 0;
            int strong = point.getStrong();
            if (strong > 0) {
                if (isFirst) {
                    drawFirstStrongF(gc, new Image("/right_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
                } else {
                    drawStrongF(gc, new Image("/right_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
                }
            } else if (strong < 0) {
                if (isFirst) {
                    drawFirstStrongF(gc, new Image("/left_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
                } else {
                    drawStrongF(gc, new Image("/left_strong_F.PNG"), centerX + point.getFx() * scale, centerY, strong);
                }
            }
        }

        saveCanvasAsImage();
        showImagePreview();
    }

    private static List<Beam> assignCoefficients(List<Beam> originalBeamList) {
        // Создание вспомогательного списка и копирование элементов из оригинального списка
        List<Beam> tempBeamList = new ArrayList<>(originalBeamList);

        // Сортировка вспомогательного списка по square в порядке возрастания
        tempBeamList.sort(Comparator.comparingDouble(Beam::getSquare));

        // Присвоение коэффициентов в порядке увеличения square
        double coefficient = 2;
        for (Beam beam : tempBeamList) {
            beam.setCoefficient(coefficient);
            coefficient += 1; // Увеличение коэффициента
        }

        // Создание нового списка для результата
        List<Beam> modifiedBeamList = new ArrayList<>();

        // Проход по исходному списку и установка коэффициентов из вспомогательного списка
        for (Beam originalBeam : originalBeamList) {
            for (Beam tempBeam : tempBeamList) {
                if (originalBeam.getSquare() == tempBeam.getSquare()) {
                    originalBeam.setCoefficient(tempBeam.getCoefficient());
                    break;
                }
            }
            modifiedBeamList.add(originalBeam);
        }
        return modifiedBeamList;
    }

    private void drawFirstStrongF(GraphicsContext gc, Image image, double x, double y, int strong) {
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        gc.drawImage(image, x, y - imageHeight / 2, imageWidth, imageHeight);
        gc.fillText(strong + "F", x + imageWidth / 2, y - 15);
    }

    private void drawStrongF(GraphicsContext gc, Image image, double x, double y, int strong) {
        double imageWidth = 30; // Фиксированная ширина изображения
        double imageHeight = 30; // Фиксированная высота изображения
        gc.drawImage(image, x - imageWidth, y - imageHeight / 2, imageWidth, imageHeight);
        gc.fillText(strong + "F", x - imageWidth / 2 - 2, y - 15);
    }

    private void drawStrongQ(GraphicsContext gc, Image image, double x1, double x2, double y, double height, int strong, double square) {
        double imageWidth = 20;
        double imageHeight = 20;
        double start = x1 - imageWidth;
        int max = (int) ((x2 - x1) / imageWidth);
        for (int i = 0; i < max; i++) {
            start += imageWidth;
            gc.drawImage(image, start, y + height / 2 - imageHeight / 2, imageWidth, imageHeight);
        }
        gc.fillText(square + "A, " + strong + "Q", (x1 + x2) / 2 - 25, y - 5);
    }

    private void saveCanvasAsImage() {
        // Получаем границы отрисованных объектов
        Rectangle2D drawingBounds = calculateDrawingBounds(beamList, pointList, centerX, centerY);

        // Создаем WritableImage с учетом границ
        WritableImage writableImage = new WritableImage((int) drawingBounds.getWidth(),
                (int) drawingBounds.getHeight());
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setViewport(new Rectangle2D(drawingBounds.getMinX(), drawingBounds.getMinY(),
                drawingBounds.getWidth(), drawingBounds.getHeight()));
        coordinateCanvas.snapshot(parameters, writableImage);

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

    private Rectangle2D calculateDrawingBounds(List<Beam> beamList, List<Point> pointList, double centerX, double centerY) {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Beam beam : beamList) {
            double x1 = centerX + beam.getX1() * scale;
            double x2 = centerX + beam.getX2() * scale;
            double y = centerY - (beam.getCoefficient() * scale) / 2;
            double height = beam.getCoefficient() * scale;

            minX = Math.min(minX, x1);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x2);
            maxY = Math.max(maxY, y + height);
        }

        for (Point point : pointList) {
            double x = centerX + point.getFx() * scale;

            minX = Math.min(minX, x);
            minY = Math.min(minY, centerY);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, centerY);
        }

        double paddingWidth = 300;
        double paddingHeight = 200;

        minX -= paddingWidth;
        minY -= paddingHeight;
        maxX += paddingWidth;
        maxY += paddingHeight;

        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }

    private void loadFromFile() throws Exception {
        pointList.clear();
        beamList.clear();
        DataFile dataFile = null;
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            dataFile = Main.convertFileToData(file, false);
        }

        if (Objects.nonNull(dataFile)) {
            pointList = dataFile.getPointList();
            beamList = dataFile.getBeamList();
            isSupportOnLeft = dataFile.isSupportOnLeft();
            isSupportOnRight = dataFile.isSupportOnRight();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Не найден файл с исходными данными. Выберите файл при помощи \"Загрузить данные из файла\" в разделе \"Добавить/изменить данные\".");
            alert.showAndWait();
            throw new Exception();
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
            stage.setTitle(Main.getDataFile().getAbsolutePath());
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}