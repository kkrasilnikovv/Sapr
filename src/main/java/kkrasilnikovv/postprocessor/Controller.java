package kkrasilnikovv.postprocessor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.processor.CalculationFile;

import java.io.File;
import java.util.Objects;

public class Controller {
    @FXML
    public TabPane tabPane;
    public Button refreshButton, loadButton, backupButton, showValueNormalVoltageButton, showGraphNormalVoltageButton;
    public Button showValueMovingButton, showGraphMovingButton, showValueStrongButton, showGraphStrongButton;
    @FXML
    private TableView<IntegerWrapper> voltageTable;

    @FXML
    private TableColumn<IntegerWrapper, Integer> idColumnNormalVoltage;

    @FXML
    private TableView<IntegerWrapper> movingTable;

    @FXML
    private TableColumn<IntegerWrapper, Integer> idColumnMoving;

    @FXML
    private TableView<IntegerWrapper> strongTable;

    @FXML
    private TableColumn<IntegerWrapper, Integer> idColumnStrong;
    private final ObservableList<IntegerWrapper> normalVoltageData = FXCollections.observableArrayList();
    private final ObservableList<IntegerWrapper> movingData = FXCollections.observableArrayList();
    private final ObservableList<IntegerWrapper> strongData = FXCollections.observableArrayList();

    public void initialize() {
        String valueButton = "Показать значение выбранного стержня";
        showValueNormalVoltageButton.setTooltip(new Tooltip(valueButton));
        showValueMovingButton.setTooltip(new Tooltip(valueButton));
        showValueStrongButton.setTooltip(new Tooltip(valueButton));

        String graphButton = "Показать график выбранного стержня";
        showGraphNormalVoltageButton.setTooltip(new Tooltip(graphButton));
        showGraphMovingButton.setTooltip(new Tooltip(graphButton));
        showGraphStrongButton.setTooltip(new Tooltip(graphButton));

        refreshButton.setTooltip(new Tooltip("Отобразить данные, которые были сгенерированы в процессоре"));
        loadButton.setTooltip(new Tooltip("Отобразить данные из файла"));
        backupButton.setTooltip(new Tooltip("Вернуться назад"));
        int sizeColumn = 797;
        idColumnNormalVoltage.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        idColumnNormalVoltage.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        idColumnNormalVoltage.setSortable(false);
        idColumnNormalVoltage.setEditable(false);
        idColumnNormalVoltage.setMinWidth(sizeColumn);
        idColumnNormalVoltage.setMaxWidth(sizeColumn);


        idColumnMoving.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        idColumnMoving.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        idColumnNormalVoltage.setSortable(false);
        idColumnNormalVoltage.setEditable(false);
        idColumnMoving.setMinWidth(sizeColumn);
        idColumnMoving.setMaxWidth(sizeColumn);

        idColumnStrong.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        idColumnStrong.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        idColumnNormalVoltage.setSortable(false);
        idColumnNormalVoltage.setEditable(false);
        idColumnStrong.setMinWidth(sizeColumn);
        idColumnStrong.setMaxWidth(sizeColumn);

        voltageTable.setItems(normalVoltageData);
        strongTable.setItems(strongData);
        movingTable.setItems(movingData);

        CalculationFile calculationFile = Main.convertFileToDataCalculation(Main.getCalculationFile());
        if (Objects.nonNull(calculationFile) && !calculationFile.isEmpty()) {
            loadFromFile(calculationFile);
        }
    }

    public void loadEvent(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters()
                .addAll(new FileChooser.ExtensionFilter("JSON Files", "*calculation.json"));
        // Открываем диалоговое окно выбора файла
        File selectedFile = fileChooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        CalculationFile calculationFile = Main.convertFileToDataCalculation(selectedFile);
        if (Objects.nonNull(calculationFile) && !calculationFile.isEmpty()) {
            loadFromFile(calculationFile);
        }
        File file = Main.getCalculationFile();
        if (Objects.nonNull(file)) {
            Main.setTitle(file.getAbsolutePath());
        }
    }

    public void loadFromFile(CalculationFile calculationFile) {

    }

    public void backup() {
        Main.showMainScene();
    }

    public void showValueNormalVoltage() {

    }

    public void showGraphNormalVoltage() {

    }

    public void showValueMoving() {

    }

    public void showGraphMoving() {

    }

    public void showValueStrong() {

    }

    public void showGraphStrong() {

    }


    public void refreshEvent() {
        File file = Main.getCalculationFile();
        if (Objects.nonNull(file)) {
            CalculationFile calculationFile = Main.convertFileToDataCalculation(file);
            if (Objects.nonNull(calculationFile) && !calculationFile.isEmpty()) {
                loadFromFile(calculationFile);
                Main.setTitle(file.getAbsolutePath());
            }
        }
    }
}
