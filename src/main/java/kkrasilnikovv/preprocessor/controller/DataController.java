package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleDoublePropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;
import kkrasilnikovv.preprocessor.model.Beam;
import kkrasilnikovv.preprocessor.model.Point;
import kkrasilnikovv.preprocessor.model.DataFile;

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DataController {
    public TabPane tabPane;
    public CheckBox checkBoxLeft, checkBoxRight;
    public Button loadButton, backupButton, addPointButton, deletePointButton, addBeamButton, deleteBeamButton;
    @FXML
    private TableView<Point> pointTable;
    @FXML
    private TableColumn<Point, Integer> idColumn, strongFColumn;
    @FXML
    private TableColumn<Point, Double> fxColumn;
    @FXML
    private TableView<Beam> beamTable;
    @FXML
    private TableColumn<Beam, Integer> beamIdColumn, startPointColumn, endPointColumn, strongQColumn;
    @FXML
    private TableColumn<Beam, Double> squareColumn, elasticityColumn, tensionColumn;
    private Gson gson;
    private int lastIdPoint = 0;
    private final ObservableList<Point> pointList = FXCollections.observableArrayList();
    private final ObservableList<Beam> beamList = FXCollections.observableArrayList();
    private final ObservableList<Integer> nodeOptions = FXCollections.observableArrayList();
    private boolean isSupportOnRight, isSupportOnLeft;


    public void initialize() {
        loadButton.setTooltip(new Tooltip("Отобразить данные из файла"));
        backupButton.setTooltip(new Tooltip("Вернуться назад и сохранить изменения,если это необходимо"));
        addPointButton.setTooltip(new Tooltip("Добавить новый узел"));
        deletePointButton.setTooltip(new Tooltip("Удалить выбранный узел"));
        addBeamButton.setTooltip(new Tooltip("Добавить новый стержень"));
        deleteBeamButton.setTooltip(new Tooltip("Удалить выбранный стержень"));
        gson = new GsonBuilder()
                .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
                .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
                .registerTypeAdapter(SimpleDoubleProperty.class, new SimpleDoublePropertyAdapter())
                .create();
        // Инициализация таблицы узлов и колонок
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        fxColumn.setCellValueFactory(cellData -> cellData.getValue().fxProperty().asObject());
        strongFColumn.setCellValueFactory(cellData -> cellData.getValue().StrongFProperty().asObject());

        idColumn.setSortable(false);
        fxColumn.setSortable(false);
        strongFColumn.setSortable(false);


        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        strongFColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Добавьте слушатель событий
        setupFxColumnEditListener();
        setupElasticityEditListener();
        setupTensionEditListener();
        setupSquareEditListener();
        // Привязываем список точек к таблице
        pointTable.setItems(pointList);

        // Инициализация таблицы стержней и колонок
        beamIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        startPointColumn.setCellValueFactory(cellData -> cellData.getValue().startPointProperty().asObject());
        endPointColumn.setCellValueFactory(cellData -> cellData.getValue().endPointProperty().asObject());
        squareColumn.setCellValueFactory(cellData -> cellData.getValue().squareProperty().asObject());
        strongQColumn.setCellValueFactory(cellData -> cellData.getValue().strongQProperty().asObject());
        elasticityColumn.setCellValueFactory(cellData -> cellData.getValue().elasticityProperty().asObject());
        tensionColumn.setCellValueFactory(cellData -> cellData.getValue().tensionProperty().asObject());

        beamIdColumn.setSortable(false);
        startPointColumn.setSortable(false);
        endPointColumn.setSortable(false);
        squareColumn.setSortable(false);
        strongQColumn.setSortable(false);
        elasticityColumn.setSortable(false);
        tensionColumn.setSortable(false);

        beamIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        squareColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        startPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        endPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        strongQColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        elasticityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        tensionColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        // Привязываем список стержней к таблице
        beamTable.setItems(beamList);
    }

    @FXML
    public void addPoint() {
        // Создаем новую точку и добавляем ее в список
        lastIdPoint++; // Вычисляем новый номер точки
        Point newPoint = new Point(lastIdPoint, 0, 0);
        pointList.add(newPoint);
        nodeOptions.add(newPoint.getId());
    }

    @FXML
    public void addBeam() {
        // Создаем новый стержень и добавляем его в список
        int newId = beamList.size() + 1; // Вычисляем новый номер стержня
        Beam newBeam = new Beam(newId, 1, 1, 1, 0, 1, 1); // Замените значения на фактические
        beamList.add(newBeam);
    }

    @FXML
    public void deletePoint() {
        if (!pointTable.getSelectionModel().isEmpty()) {
            int selectedIndex = pointTable.getSelectionModel().getSelectedIndex();
            pointTable.getItems().remove(selectedIndex);
            nodeOptions.remove(selectedIndex);
            lastIdPoint = pointTable.getItems().stream().mapToInt(Point::getId).max().orElse(0);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.setContentText("Узел не выбран. Выберите узел в таблице кликом по нему.");
            alert.showAndWait();
        }
    }

    @FXML
    public void deleteBeam() {
        if (!beamTable.getSelectionModel().isEmpty()) {
            int selectedIndex = beamTable.getSelectionModel().getSelectedIndex();
            beamTable.getItems().remove(selectedIndex);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.setContentText("Стержень не выбран. Выберите стержень в таблице кликом по нему.");
            alert.showAndWait();
        }
    }

    public void backup() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение сохранения");
        alert.setHeaderText("Сохранить изменения перед выходом?");

        ButtonType saveButton = new ButtonType("Да");
        ButtonType discardButton = new ButtonType("Нет");

        alert.getButtonTypes().setAll(saveButton, discardButton);

        // Ожидание реакции пользователя на диалоговое окно
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {
            // Пользователь выбрал "Да", сохраняем файл
            try {
                saveToFile();
                Main.showScene(PreProcessor.getInstance().getMainScene());
            } catch (Exception e) {

            }
        } else if (result.isPresent() && result.get() == discardButton) {
            // Пользователь выбрал "Нет", просто возвращаемся назад без сохранения
            Main.showScene(PreProcessor.getInstance().getMainScene());
        }
    }

    public void saveToFile() throws Exception {
        File file = Main.getDataFile();
        if (Objects.isNull(file)) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите папку для сохранения файла");

            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                // Отображаем диалоговое окно для ввода имени файла
                TextInputDialog dialog = new TextInputDialog("data");
                dialog.setTitle("Имя файла");
                dialog.setHeaderText("Введите имя файла:");
                dialog.setContentText("Имя файла:");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    String fileName = result.get();
                    file = new File(selectedDirectory, fileName + ".json");
                } else {
                    buildErrorAlert("Имя файла не выбрано.");
                }
            } else {
                buildErrorAlert("Директория не выбрана.");
            }
        }

        List<Beam> beams = this.beamList.stream().toList();
        List<Point> points = this.pointList.stream().toList();
        boolean isNotEmptySupports = isSupportOnLeft || isSupportOnRight;
        // Проверка, что у каждого объекта startPoint не равен endPoint
        boolean isNotCyclical = beams
                .stream()
                .allMatch(beam -> beam.getStartPoint() != beam.getEndPoint());
        boolean isContainsPoint = true;
        for (Beam beam : beams) {
            if (containsId(points, beam.getStartPoint())) {
                isContainsPoint = false;
            }
            if (containsId(points, beam.getEndPoint())) {
                isContainsPoint = false;
            }
        }
        if (points.size() < 2) {
            buildErrorAlert("Конструкция не может содержать менее 2 точек.");
        }
        if (points.size() - 1 != beams.size()) {
            buildErrorAlert("Не выполняется правило \"Кол-во точек должно быть больше на 1,чем кол-во стержней.\"");
        }
        if (!isNotCyclical) {
            buildErrorAlert("Стержень не может начинаться и заканчиваться в одной точке.");
        }
        if (!isContainsPoint) {
            buildErrorAlert("Стержень начинается или заканчивается в несуществующей точке.");
        }
        if (!isNotEmptySupports) {
            buildErrorAlert("Конструкция не может существовать хотя бы без 1 поддержки.");
        }

        beams.forEach(beam -> points.forEach(point -> {
            if (beam.getStartPoint() == point.getId()) {
                beam.setX1(point.getFx());
            }
            if (beam.getEndPoint() == point.getId()) {
                beam.setX2(point.getFx());
            }
            beam.setLength();
        }));

        for (Beam beam : beams) {
            if (beam.getX2() - beam.getX1() <= 0) {
                buildErrorAlert("Стержень №" + beam.getId().get() + "имеет длину менее или равной 0.");
            }
        }
        writeToFile(file, points, beams);
    }

    private boolean containsId(List<Point> points, Integer id) {
        for (Point point : points) {
            if (point.getId() == id) {
                return false;
            }
        }
        return true;
    }

    private void writeToFile(File file, List<Point> pointList, List<Beam> beamList) {
        Alert alert;
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(gson.toJson(new DataFile(pointList, beamList,
                    lastIdPoint, isSupportOnLeft, isSupportOnRight)));
            Main.setDataFile(file);
            Main.setTitle(file.getAbsolutePath());
            alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Данные успешно сохранены");
            alert.setHeaderText("Сохранено в файл: " + file.getAbsolutePath());
            alert.showAndWait();
        } catch (IOException e) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Невозможно сохранить в файл:" + file.getName());
            alert.showAndWait();
            loadFromFile(Main.convertFileToData(Main.getDataFile(), true));
        }
    }

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*data.json"));
        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        DataFile dataFile = Main.convertFileToData(selectedFile, true);
        if (Objects.nonNull(dataFile) && !dataFile.isEmpty()) {
            loadFromFile(dataFile);
        }
        File file = Main.getDataFile();
        if (Objects.nonNull(file)) {
            Main.setTitle(file.getAbsolutePath());
        }
    }

    private void loadFromFile(DataFile dataFile) {
        pointList.clear();
        beamList.clear();
        nodeOptions.clear();
        if (Objects.nonNull(dataFile)) {
            pointList.addAll(dataFile.getPointList());
            beamList.addAll(dataFile.getBeamList());
            lastIdPoint = dataFile.getLastIdPoint();
            for (Point point : pointList) {
                nodeOptions.add(point.getId());
            }
            isSupportOnRight = dataFile.isSupportOnRight();
            isSupportOnLeft = dataFile.isSupportOnLeft();
            checkBoxRight.setSelected(isSupportOnRight);
            checkBoxLeft.setSelected(isSupportOnLeft);
        }
        pointTable.refresh();
        beamTable.refresh();
    }

    private void setupFxColumnEditListener() {
        fxColumn.setOnEditCommit(event -> {
            Point editedPoint = event.getRowValue();
            Double newValue = event.getNewValue();

            if (newValue != null && newValue >= 0) {
                // Примените новое значение, если оно прошло проверку
                editedPoint.setFx(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                fxColumn.getTableView().getItems().set(event.getTablePosition().getRow(), editedPoint);
            }
        });
    }

    private void setupElasticityEditListener() {
        elasticityColumn.setOnEditCommit(event -> {
            Beam beam = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null && newValue >= 1) {
                // Примените новое значение, если оно прошло проверку
                beam.setElasticity(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                elasticityColumn.getTableView().getItems().set(event.getTablePosition().getRow(), beam);
            }
        });
    }

    private void setupSquareEditListener() {
        squareColumn.setOnEditCommit(event -> {
            Beam beam = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null && newValue >= 1) {
                // Примените новое значение, если оно прошло проверку
                beam.setSquare(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                squareColumn.getTableView().getItems().set(event.getTablePosition().getRow(), beam);
            }
        });
    }

    private void setupTensionEditListener() {
        tensionColumn.setOnEditCommit(event -> {
            Beam beam = event.getRowValue();
            Double newValue = event.getNewValue();
            if (newValue != null && newValue >= 1) {
                // Примените новое значение, если оно прошло проверку
                beam.setTension(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                tensionColumn.getTableView().getItems().set(event.getTablePosition().getRow(), beam);
            }
        });
    }

    private void buildErrorAlert(String text) throws Exception {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Данные не будут сохранены");
        alert.setHeaderText(text);
        alert.showAndWait();
        throw new Exception();
    }

    public void checkBoxLeft() {
        isSupportOnLeft = checkBoxLeft.isSelected();
    }

    public void checkBoxRight() {
        isSupportOnRight = checkBoxRight.isSelected();
    }
}