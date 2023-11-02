package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.prorepty_adapter.SimpleStringPropertyAdapter;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.SavingFile;
import kkrasilnikovv.preprocessor.model.SectionType;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DataController {
    public TabPane tabPane;
    public CheckBox checkBoxLeft, checkBoxRight;
    @FXML
    private TableView<PointData> pointTable;

    @FXML
    private TableColumn<PointData, Integer> idColumn, fxColumn, strongFColumn;

    @FXML
    private TableView<BeamData> beamTable;
    @FXML
    private TableColumn<BeamData, Integer> beamIdColumn, startPointColumn, endPointColumn, widthColumn, strongQColumn;
    @FXML
    private TableColumn<BeamData, String> typeColumn;
    private Gson gson;
    private int lastIdPoint = 0;
    private final ObservableList<PointData> pointList = FXCollections.observableArrayList();
    private final ObservableList<BeamData> beamList = FXCollections.observableArrayList();
    private final ObservableList<Integer> nodeOptions = FXCollections.observableArrayList();
    private boolean isSupportOnRight, isSupportOnLeft;
    private final ObservableList<String> sectionTypeOptions = FXCollections.observableArrayList();


    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
                .registerTypeAdapter(SimpleStringProperty.class, new SimpleStringPropertyAdapter())
                .create();
        // Инициализация таблицы узлов и колонок
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        fxColumn.setCellValueFactory(cellData -> cellData.getValue().fxProperty().asObject());
        strongFColumn.setCellValueFactory(cellData -> cellData.getValue().StrongFProperty().asObject());

        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        strongFColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Добавьте слушатель событий к столбцу fxColumn
        setupFxColumnEditListener();
        setupWidthEditListener();
        // Привязываем список точек к таблице
        pointTable.setItems(pointList);

        // Инициализация таблицы стержней и колонок
        beamIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        startPointColumn.setCellValueFactory(cellData -> cellData.getValue().startPointProperty().asObject());
        endPointColumn.setCellValueFactory(cellData -> cellData.getValue().endPointProperty().asObject());
        widthColumn.setCellValueFactory(cellData -> cellData.getValue().widthProperty().asObject());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().sectionTypeProperty());
        strongQColumn.setCellValueFactory(cellData -> cellData.getValue().strongQProperty().asObject());

        beamIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        widthColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        startPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        endPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        typeColumn.setCellFactory(ComboBoxTableCell.forTableColumn(sectionTypeOptions));
        strongQColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Привязываем список стержней к таблице
        beamTable.setItems(beamList);
        sectionTypeOptions.addAll(Arrays.stream(SectionType.values())
                .map(SectionType::toString)
                .toList());
        File file =  Main.getDataFile();
        if(Objects.nonNull(file)) {
            loadFromFile(Main.convertFileToData(file));
        }
    }

    @FXML
    public void addPoint() {
        // Создаем новую точку и добавляем ее в список
        lastIdPoint++; // Вычисляем новый номер точки
        PointData newPoint = new PointData(lastIdPoint, 0, 0);
        pointList.add(newPoint);
        nodeOptions.add(newPoint.getId());
    }

    @FXML
    public void addBeam() {
        // Создаем новый стержень и добавляем его в список
        int newId = beamList.size() + 1; // Вычисляем новый номер стержня
        BeamData newBeam = new BeamData(newId, 0, 0, 0, SectionType.TRIANGLE.toString(), 0); // Замените значения на фактические
        beamList.add(newBeam);
    }

    @FXML
    public void deletePoint() {
        if (!pointTable.getSelectionModel().isEmpty()) {
            int selectedIndex = pointTable.getSelectionModel().getSelectedIndex();
            pointTable.getItems().remove(selectedIndex);
            nodeOptions.remove(selectedIndex);
        }
    }

    @FXML
    public void deleteBeam() {
        if (!beamTable.getSelectionModel().isEmpty()) {
            int selectedIndex = beamTable.getSelectionModel().getSelectedIndex();
            beamTable.getItems().remove(selectedIndex);
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
            saveToFile();
            Main.showScene(PreProcessor.getInstance().getMainScene());
        } else if (result.isPresent() && result.get() == discardButton) {
            // Пользователь выбрал "Нет", просто возвращаемся назад без сохранения
            Main.showScene(PreProcessor.getInstance().getMainScene());
        }
    }

    public void saveToFile() {
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Данные не будут сохранены");
                    alert.setHeaderText("Имя файла не выбрано.");
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Данные не будут сохранены");
                alert.setHeaderText("Директория не выбрана.");
            }
        }

        List<BeamData> beamDataList = beamList.stream().toList();

        // Проверка, что у всех объектов одинаковый тип сечения
        boolean isOneType = beamDataList.stream()
                .map(BeamData::getSectionType)
                .distinct()
                .count() == 1;

        // Проверка, что у каждого объекта startPoint не равен endPoint
        boolean isNotCyclical = beamDataList.stream()
                .allMatch(beamData -> beamData.getStartPoint() != beamData.getEndPoint());

        if (!isNotCyclical) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Стержень не может начинаться и заканчиваться в одной точке.");
            alert.showAndWait();
        }
        if (!isOneType) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Стержни не могут иметь разный тип сечения.");
            alert.showAndWait();
        }
        if (isNotCyclical && isOneType) {
            List<PointData> pointDataList = pointList.stream().toList();
            beamDataList.forEach(beam -> pointDataList.forEach(point -> {
                if (beam.getStartPoint() == point.getId()) {
                    beam.setX1(point.getFx());
                }
                if (beam.getEndPoint() == point.getId()) {
                    beam.setX2(point.getFx());
                }
            }));
            writeToFile(file, pointDataList, beamDataList);
        }
    }

    private void writeToFile(File file, List<PointData> pointDataList, List<BeamData> beamDataList) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(new SavingFile(pointDataList, beamDataList,
                    lastIdPoint, isSupportOnLeft, isSupportOnRight)));
            fileWriter.close();
            Main.setDataFile(file);
            System.out.println("Сохранено в файл: " + file.getAbsolutePath());
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Данные не будут сохранены");
            alert.setHeaderText("Невозможно сохранить в файл:" + file.getName());
        }
    }

    public void loadFromFile(SavingFile savingFile) {
        pointList.clear();
        beamList.clear();
        nodeOptions.clear();
        if (Objects.nonNull(savingFile)) {
            pointList.addAll(savingFile.getPointList());
            beamList.addAll(savingFile.getBeamList());
            lastIdPoint = savingFile.getLastIdPoint();
            for (PointData pointData : pointList) {
                nodeOptions.add(pointData.getId());
            }
            isSupportOnRight = savingFile.isSupportOnRight();
            isSupportOnLeft = savingFile.isSupportOnLeft();
            checkBoxRight.setSelected(isSupportOnRight);
            checkBoxLeft.setSelected(isSupportOnLeft);
        }
        pointTable.refresh();
        beamTable.refresh();
    }

    private void setupFxColumnEditListener() {
        fxColumn.setOnEditCommit(event -> {
            PointData editedPoint = event.getRowValue();
            Integer newValue = event.getNewValue();

            if (newValue != null && newValue >= 0) {
                // Примените новое значение, если оно прошло проверку
                editedPoint.setFx(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                fxColumn.getTableView().getItems().set(event.getTablePosition().getRow(), editedPoint);
            }
        });
    }

    private void setupWidthEditListener() {
        widthColumn.setOnEditCommit(event -> {
            BeamData beamData = event.getRowValue();
            Integer newValue = event.getNewValue();
            if (newValue != null && newValue >= 1) {
                // Примените новое значение, если оно прошло проверку
                beamData.setWidth(newValue);
            } else {
                // Отклоните изменение и восстановите предыдущее значение, если новое значение не прошло проверку
                widthColumn.getTableView().getItems().set(event.getTablePosition().getRow(), beamData);
            }
        });
    }

    public void checkBoxLeft() {
        isSupportOnLeft = checkBoxLeft.isSelected();
    }

    public void checkBoxRight() {
        isSupportOnRight = checkBoxRight.isSelected();
    }
}