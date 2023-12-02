package kkrasilnikovv.postprocessor;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.processor.CalculationFile;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Controller {
    @FXML
    public TabPane tabPane;

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
    private Map<Integer, List<NormalVoltageData>> normalVoltage;
    private Map<Integer, List<LongitudinalStrongData>> longitudinalStrong;
    private Map<Integer, List<MovingData>> moving;
    private final ObservableList<IntegerWrapper> normalVoltageData = FXCollections.observableArrayList();
    private final ObservableList<IntegerWrapper> movingData = FXCollections.observableArrayList();
    private final ObservableList<IntegerWrapper> strongData = FXCollections.observableArrayList();

    public void initialize() {
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
        this.normalVoltage = calculationFile.getNormalVoltage();
        this.moving = calculationFile.getMoving();
        this.longitudinalStrong = calculationFile.getLongitudinalStrong();
        refreshTables();
    }

    private void refreshTables() {
        normalVoltageData.clear();
        movingData.clear();
        strongData.clear();

        normalVoltageData.addAll(normalVoltage.keySet().stream().map(IntegerWrapper::new).toList());
        movingData.addAll(moving.keySet().stream().map(IntegerWrapper::new).toList());
        strongData.addAll(longitudinalStrong.keySet().stream().map(IntegerWrapper::new).toList());

        voltageTable.refresh();
        strongTable.refresh();
        movingTable.refresh();
    }

    public void backup() {
        Main.showMainScene();
    }

    public void showValueNormalVoltage() {
        DataInTable result = getDataInTable(voltageTable, normalVoltage);
        createTable("Нормальное напряжение на стержне №" + result.getIndex(),
                result.getX(), "σx", result.getValue());
    }

    public void showGraphNormalVoltage() {
        DataInTable result = getDataInTable(voltageTable, normalVoltage);
        createSmoothChart("Нормальное напряжение на стержне №" + result.getIndex(), "σx",
                "Нормальное напряжение в точке", result.getX(), result.getValue());
    }

    public void showValueMoving() {
        DataInTable result = getDataInTable(movingTable, moving);
        createTable("Перемещения на стержне №" + result.getIndex(),
                result.getX(), "Ux", result.getValue());
    }

    public void showGraphMoving() {
        DataInTable result = getDataInTable(movingTable, moving);
        createSmoothChart("Перемещения на стержне №" + result.getIndex(), "Ux",
                "Перемещение в точке", result.getX(), result.getValue());
    }

    public void showValueStrong() {
        DataInTable result = getDataInTable(strongTable, longitudinalStrong);
        createTable("Продольные силы на стержне №" + result.getIndex()
                , result.getX(), "Nx", result.getValue());
    }

    public void showGraphStrong() {
        DataInTable result = getDataInTable(strongTable, longitudinalStrong);
        createSmoothChart("Продольные силы на стержне №" + result.getIndex(), "Nx",
                "Продольные силы в точке", result.getX(), result.getValue());
    }

    private <T extends Data> DataInTable getDataInTable(TableView<?> tableView, Map<Integer, List<T>> data) {
        if (!tableView.getSelectionModel().isEmpty()) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            List<T> strongDataList = data.get(selectedIndex + 1);
            List<Double> x = strongDataList.stream().map(Data::getX).toList();
            List<Double> values = strongDataList.stream().map(Data::getValue).toList();
            return new DataInTable((selectedIndex + 1), x, values);
        } else {
            return new DataInTable();
        }
    }

    private void createSmoothChart(String title, String y, String description, List<Double> xData, List<Double> yData) {
        Stage stage = new Stage();

        // Создаем оси X и Y
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("X");
        yAxis.setLabel(y);

        // Создаем график
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        // Создаем серию данных
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(description);

        // Добавляем данные в серию
        for (int i = 0; i < xData.size(); i++) {
            series.getData().add(new XYChart.Data<>(xData.get(i), yData.get(i)));
        }

        // Добавляем серию к графику
        lineChart.getData().add(series);

        // Создаем сцену и устанавливаем на нее график
        Scene scene = new Scene(lineChart, 600, 400);
        stage.setScene(scene);

        // Показываем окно
        stage.show();
    }

    private void createTable(String stageName, List<Double> value1, String columnName2, List<Double> value2) {
        ObservableList<CustomData> data = FXCollections.observableArrayList();
        for (int i = 0; i < value1.size(); i++) {
            data.add(new CustomData(value1.get(i), value2.get(i)));
        }
        TableView<CustomData> table = new TableView<>();
        TableColumn<CustomData, Double> column1 = new TableColumn<>("L(x)");

        column1.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());
        column1.setMinWidth(197);
        column1.setSortable(false);

        TableColumn<CustomData, Double> column2 = new TableColumn<>(columnName2);

        column2.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        column2.setMinWidth(197);
        column2.setSortable(false);

        table.getColumns().addAll(column1, column2);
        table.setItems(data);

        Scene scene = new Scene(new VBox(table), 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(stageName);
        stage.show();
    }

    public static class CustomData {
        private final SimpleDoubleProperty x;
        private final SimpleDoubleProperty value;

        public CustomData(double x, double value) {
            this.value = new SimpleDoubleProperty(value);
            this.x = new SimpleDoubleProperty(x);
        }

        public SimpleDoubleProperty valueProperty() {
            return value;
        }

        public SimpleDoubleProperty xProperty() {
            return x;
        }
    }
}
