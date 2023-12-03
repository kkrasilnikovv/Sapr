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
    private Map<Integer, List<NormalVoltageData>> normalVoltage;
    private Map<Integer, List<LongitudinalStrongData>> longitudinalStrong;
    private Map<Integer, List<MovingData>> moving;
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
        if (!result.isEmpty()) {
            createTable("Нормальное напряжение на стержне №" + result.getIndex(),
                    result.getX(), "σx", result.getValue());
        }
    }

    public void showGraphNormalVoltage() {
        DataInTable result = getDataInTable(voltageTable, normalVoltage);
        if (!result.isEmpty()) {
            createSmoothChart("Нормальное напряжение на стержне №" + result.getIndex(), "σx",
                    "Нормальное напряжение в точке", result.getX(), result.getValue());
        }
    }

    public void showValueMoving() {
        DataInTable result = getDataInTable(movingTable, moving);
        if (!result.isEmpty()) {
            createTable("Перемещения на стержне №" + result.getIndex(),
                    result.getX(), "Ux", result.getValue());
        }
    }

    public void showGraphMoving() {
        DataInTable result = getDataInTable(movingTable, moving);
        if (!result.isEmpty()) {
            createSmoothChart("Перемещения на стержне №" + result.getIndex(), "Ux",
                    "Перемещение в точке", result.getX(), result.getValue());
        }
    }

    public void showValueStrong() {
        DataInTable result = getDataInTable(strongTable, longitudinalStrong);
        if (!result.isEmpty()) {
            createTable("Продольные силы на стержне №" + result.getIndex()
                    , result.getX(), "Nx", result.getValue());
        }
    }

    public void showGraphStrong() {
        DataInTable result = getDataInTable(strongTable, longitudinalStrong);
        if (!result.isEmpty()) {
            createSmoothChart("Продольные силы на стержне №" + result.getIndex(), "Nx",
                    "Продольные силы в точке", result.getX(), result.getValue());
        }
    }

    private <T extends Data> DataInTable getDataInTable(TableView<?> tableView, Map<Integer, List<T>> data) {
        if (!tableView.getSelectionModel().isEmpty()) {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            List<T> strongDataList = data.get(selectedIndex + 1);
            List<Double> x = strongDataList.stream().map(Data::getX).toList();
            List<Double> values = strongDataList.stream().map(Data::getValue).toList();
            return new DataInTable((selectedIndex + 1), x, values);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.setContentText("Стержень не выбран. Выберите стержень в таблице кликом по нему.");
            alert.showAndWait();
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

    private void createTable(String stageName, List<Double> length, String columnValueName, List<Double> value) {
        ObservableList<CustomData> data = FXCollections.observableArrayList();
        for (int i = 0; i < length.size(); i++) {
            data.add(new CustomData(length.get(i), value.get(i)));
        }
        TableView<CustomData> table = new TableView<>();
        TableColumn<CustomData, Double> lengthColumn = new TableColumn<>("L(x)");

        lengthColumn.setCellValueFactory(cellData -> cellData.getValue().xProperty().asObject());
        lengthColumn.setMinWidth(197);
        lengthColumn.setSortable(false);

        TableColumn<CustomData, Double> valueColumn = new TableColumn<>(columnValueName);

        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        valueColumn.setMinWidth(197);
        valueColumn.setSortable(false);

        table.getColumns().addAll(lengthColumn, valueColumn);
        table.setItems(data);

        Scene scene = new Scene(new VBox(table), 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(stageName);
        stage.show();
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
