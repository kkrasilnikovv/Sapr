package kkrasilnikovv.preprocessor.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.preprocessor.PreProcessor;
import kkrasilnikovv.preprocessor.SimpleIntegerPropertyAdapter;
import kkrasilnikovv.preprocessor.model.BeamData;
import kkrasilnikovv.preprocessor.model.PointData;
import kkrasilnikovv.preprocessor.model.SavingFile;

import java.io.*;

public class DataController {
    public TabPane tabPane;
    @FXML
    private TableView<PointData> pointTable;

    @FXML
    private TableColumn<PointData, Integer> idColumn;

    @FXML
    private TableColumn<PointData, Integer> fxColumn, fyColumn;

    @FXML
    private TableView<BeamData> beamTable;

    @FXML
    private TableColumn<BeamData, Integer> beamIdColumn, startPointColumn, endPointColumn;
    private Gson gson;
    private int lastIdPoint = 0;
    private final ObservableList<PointData> pointList = FXCollections.observableArrayList();
    private final ObservableList<BeamData> beamList = FXCollections.observableArrayList();
    private final ObservableList<Integer> nodeOptions = FXCollections.observableArrayList();

    public void initialize() {
        gson = new GsonBuilder()
                .registerTypeAdapter(SimpleIntegerProperty.class, new SimpleIntegerPropertyAdapter())
                .create();
        // Инициализация таблицы узлов и колонок
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        fxColumn.setCellValueFactory(cellData -> cellData.getValue().fxProperty().asObject());
        fyColumn.setCellValueFactory(cellData -> cellData.getValue().fyProperty().asObject());

        idColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fxColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        fyColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        // Привязываем список точек к таблице
        pointTable.setItems(pointList);

        // Инициализация таблицы стержней и колонок
        beamIdColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        startPointColumn.setCellValueFactory(cellData -> cellData.getValue().startPointProperty().asObject());
        endPointColumn.setCellValueFactory(cellData -> cellData.getValue().endPointProperty().asObject());

        beamIdColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        /// TODO: 22.10.2023 Реализовать при сохранении файла проверку что нет стержня 1:1
        startPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));
        endPointColumn.setCellFactory(ComboBoxTableCell.forTableColumn(new IntegerStringConverter(), nodeOptions));

        // Привязываем список стержней к таблице
        beamTable.setItems(beamList);
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
        BeamData newBeam = new BeamData(newId, 0, 0); // Замените значения на фактические
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
        Main.showScene(PreProcessor.getInstance().getMainScene());
    }

    public void saveToFile() {
        try {
            // Создайте объект ObjectMapper для преобразования объектов в JSON
            // Сохраните JSON в файл
            FileWriter fileWriter = new FileWriter("data.json");
            fileWriter.write(gson.toJson(new SavingFile(pointList.stream().toList(), beamList.stream().toList(), lastIdPoint)));
            fileWriter.close();
            System.out.println("Сохранено в файл: data.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile(SavingFile savingFile) {
        pointList.clear();
        beamList.clear();
        nodeOptions.clear();

        pointList.addAll(savingFile.getPointList());
        beamList.addAll(savingFile.getBeamList());
        lastIdPoint = savingFile.getLastIdPoint();
        for (PointData pointData : pointList) {
            nodeOptions.add(pointData.getId());
        }

        pointTable.refresh();
        beamTable.refresh();
    }

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.json"));

        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (selectedFile != null) {
            // В этом месте можно выполнить действия с выбранным файлом
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                SavingFile savingFile = gson.fromJson(reader, SavingFile.class);
                loadFromFile(savingFile);
            } catch (FileNotFoundException e) {
                System.out.println("Файл не найден.");
            }
        } else {
            System.out.println("No file selected.");
        }
    }
}
