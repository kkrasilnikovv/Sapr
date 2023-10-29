package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.SavingFile;

import java.io.*;
import java.util.Objects;
import java.util.Optional;

public class DataController {
    public TabPane tabPane;
    public CheckBox checkBoxLeft, checkBoxRight;
    @FXML
    private TableView<PointData> pointTable;

    @FXML
    private TableColumn<PointData, Integer> idColumn;

    @FXML
    private TableColumn<PointData, Integer> fxColumn;

    @FXML
    private TableView<BeamData> beamTable;
    @FXML
    private TableColumn<BeamData, Integer> beamIdColumn, startPointColumn, endPointColumn,widthColumn;
    private Gson gson;
    private int lastIdPoint = 0;
    private final ObservableList<PointData> pointList = FXCollections.observableArrayList();
    private final ObservableList<BeamData> beamList = FXCollections.observableArrayList();
    private final ObservableList<Integer> nodeOptions = FXCollections.observableArrayList();
    private boolean isSupportOnRight, isSupportOnLeft;

    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
                .create();
        // Инициализация таблицы узлов и колонок
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        fxColumn.setCellValueFactory(cellData -> cellData.getValue().fxProperty().asObject());

        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // Добавьте слушатель событий к столбцу fxColumn
        setupFxColumnEditListener();
        setupWidthEditListener();
        // Привязываем список точек к таблице
        pointTable.setItems(pointList);

        // Инициализация таблицы стержней и колонок
        beamIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        startPointColumn.setCellValueFactory(cellData -> cellData.getValue().startPointProperty().asObject());
        endPointColumn.setCellValueFactory(cellData -> cellData.getValue().endPointProperty().asObject());
        widthColumn.setCellValueFactory(cellData -> cellData.getValue().width().asObject());

        beamIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        widthColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        startPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        endPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));

        // Привязываем список стержней к таблице
        beamTable.setItems(beamList);
        loadFromFile(Main.getDataGSONFile());
    }

    @FXML
    public void addPoint() {
        // Создаем новую точку и добавляем ее в список
        lastIdPoint++; // Вычисляем новый номер точки
        PointData newPoint = new PointData(lastIdPoint, 0);
        pointList.add(newPoint);
        nodeOptions.add(newPoint.getId());
    }

    @FXML
    public void addBeam() {
        // Создаем новый стержень и добавляем его в список
        int newId = beamList.size() + 1; // Вычисляем новый номер стержня
        BeamData newBeam = new BeamData(newId, 0, 0,0); // Замените значения на фактические
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
        try {
            File file = Main.getDataFile();
            FileWriter fileWriter;
            String fileName = "data.json";
            if (Objects.nonNull(file)) {
                fileWriter = new FileWriter(file);
            } else {
                fileWriter = new FileWriter(fileName);
            }
            fileWriter.write(gson.toJson(new SavingFile(pointList.stream().toList(), beamList.stream().toList(),
                    lastIdPoint, isSupportOnLeft, isSupportOnRight)));
            fileWriter.close();
            System.out.println("Сохранено в файл: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
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