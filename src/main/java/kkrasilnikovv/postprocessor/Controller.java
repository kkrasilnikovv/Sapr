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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import kkrasilnikovv.main.Main;
import kkrasilnikovv.processor.CalculationFile;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller {
    @FXML
    public TabPane tabPane;
    public Button refreshButton, loadButton, backupButton, showValueNormalVoltageButton, showGraphNormalVoltageButton;
    public Button showValueMovingButton, showGraphMovingButton, showValueStrongButton, showGraphStrongButton;
    public Button calculateNormalVoltageButton, calculateMovingButton, calculateStrongButton;
    public TextField inputXNormalVoltage, inputXMoving, inputXStrong;
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
    private Map<Integer, Double[]> normalVoltageMap;
    private Map<Integer, Double[]> longitudinalStrongMap;
    private Map<Integer, Double[]> movingMap;
    private Map<Integer, Double> predelVoltage;
    private Map<Integer, Double> startPoints;
    private Map<Integer, Double> endPoints;

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
        normalVoltageMap = extractLinearCoefficients(calculationFile.getNormalVoltage());
        longitudinalStrongMap = extractLinearCoefficients(calculationFile.getLongitudinalStrong());
        movingMap = extractQuadraticCoefficients(calculationFile.getMoving());
        predelVoltage = calculationFile.getPredelVoltage();
        startPoints = calculationFile.getStartPoint();
        endPoints = calculationFile.getEndPoint();
        refreshTable();
    }

    public void backup() {
        Main.showMainScene();
    }

    public void showValueNormalVoltage() {
        Integer index = getSelectedIndex(voltageTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateNormalVoltage(index, length);
            double predVoltage = predelVoltage.get(index);
            createTable("Нормальное напряжение на стержне №" + index,
                    length, "σx", value, true, predVoltage);
        }
    }

    private List<Double> calculateNormalVoltage(Integer index, List<Double> length) {
        List<Double> result = new ArrayList<>();
        Double[] value = normalVoltageMap.get(index);
        for (Double v : length) {
            result.add(round(value[0] * v + value[1]));
        }
        return result;
    }

    private List<Double> divideSegment(double start, double end) {
        List<Double> points = new ArrayList<>();
        double delta = (end - start) / (10 - 1);

        for (int i = 0; i < 10; i++) {
            points.add(round(start + i * delta));
        }

        return points;
    }

    public void showGraphNormalVoltage() {
        Integer index = getSelectedIndex(voltageTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateNormalVoltage(index, length);
            createSmoothChart("Нормальное напряжение на стержне №" + index, "σx",
                    "Нормальное напряжение в точке", length, value);
        }
    }

    public void showValueMoving() {
        Integer index = getSelectedIndex(movingTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateMoving(index, length);
            createTable("Перемещения на стержне №" + index,
                    length, "Ux", value, false, 0);
        }
    }

    private List<Double> calculateMoving(Integer index, List<Double> length) {
        List<Double> result = new ArrayList<>();
        Double[] value = movingMap.get(index);
        for (Double v : length) {
            result.add(round(value[0] * Math.pow(v, 2) + value[1] * v + value[2]));
        }
        return result;
    }

    public void showGraphMoving() {
        Integer index = getSelectedIndex(movingTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateMoving(index, length);
            createSmoothChart("Перемещения на стержне №" + index, "Ux",
                    "Перемещение в точке", length, value);
        }
    }

    public void showValueStrong() {
        Integer index = getSelectedIndex(strongTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateStrong(index, length);
            createTable("Перемещения на стержне №" + index,
                    length, "Ux", value, false, 0);
        }
    }

    private List<Double> calculateStrong(Integer index, List<Double> length) {
        List<Double> result = new ArrayList<>();
        Double[] value = longitudinalStrongMap.get(index);
        for (Double v : length) {
            result.add(round(value[0] * v + value[1]));
        }
        return result;
    }

    public void showGraphStrong() {
        Integer index = getSelectedIndex(strongTable);
        if (Objects.nonNull(index)) {
            index += 1;
            List<Double> length = divideSegment(startPoints.get(index), endPoints.get(index));
            List<Double> value = calculateStrong(index, length);
            createSmoothChart("Продольные силы на стержне №" + index, "Nx",
                    "Продольные силы в точке", length, value);
        }
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

    public void calculateNormalVoltageInPoint() {
        String inputXText = inputXNormalVoltage.getText();
        if (Objects.nonNull(startPoints)) {
            try {
                double x = Double.parseDouble(inputXText);
                int index = 0;
                for (int i = 1; i < startPoints.size()+1; i++) {
                    double startPoint = startPoints.get(i);
                    double endPoint = endPoints.get(i);
                    if (x <= endPoint && x >= startPoint) {
                        index = i;
                        break;
                    }
                }
                if (index > 0) {
                    List<Double> value = calculateNormalVoltage(index, List.of(x));
                    double predVoltage = predelVoltage.get(index);
                    createTable("Нормальное напряжение в точке " + round(x),
                            List.of(x), "σx", value, true, predVoltage);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Предупреждение");
                    alert.setHeaderText(null);
                    alert.setContentText(String.format("Значение Х не входит в конструкцию [%s,%s]",
                            startPoints.get(1), endPoints.get(endPoints.size() - 1)));
                    alert.showAndWait();
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.setContentText("Значение Х имеет не верный формат.");
                alert.showAndWait();
            }
        }
    }

    public static Map<Integer, Double[]> extractLinearCoefficients(Map<Integer, String> data) {
        Map<Integer, Double[]> result = new HashMap<>();

        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            Integer entryKey = entry.getKey();
            String entryValue = entry.getValue();
            Double[] coefficients = extractLinearCoefficients(entryValue);
            result.put(entryKey, coefficients);
        }

        return result;
    }

    private double round(Double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private void refreshTable() {
        normalVoltageData.clear();
        movingData.clear();
        strongData.clear();

        normalVoltageData.addAll(normalVoltageMap.keySet().stream().map(IntegerWrapper::new).toList());
        movingData.addAll(movingMap.keySet().stream().map(IntegerWrapper::new).toList());
        strongData.addAll(longitudinalStrongMap.keySet().stream().map(IntegerWrapper::new).toList());

        voltageTable.refresh();
        strongTable.refresh();
        movingTable.refresh();
    }

    private void createTable(String stageName, List<Double> length, String columnValueName, List<Double> value, boolean isVoltage, double predelVoltage) {
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

        // Пример: покрасить ячейки в столбце valueColumn в красный, если isVoltage и значение больше predelVoltage
        if (isVoltage) {
            valueColumn.setCellFactory(column -> new javafx.scene.control.cell.TextFieldTableCell<>() {
                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item != null && Math.abs(item) > Math.abs(predelVoltage)) {
                        // Поменяйте цвет фона или текста на ваш выбор
                        setStyle("-fx-background-color: #f67d7d;");
                    } else {
                        setStyle(""); // Вернуть стиль по умолчанию
                    }
                }
            });
        }

        table.getColumns().addAll(lengthColumn, valueColumn);
        table.setItems(data);

        Scene scene = new Scene(new VBox(table), 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(stageName);
        stage.show();
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

    private static Double[] extractLinearCoefficients(String expression) {
        Pattern pattern = Pattern.compile("\\((-?\\d*\\.?\\d*)\\)\\*x\\+\\((-?\\d*\\.?\\d*)\\)");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.matches()) {
            double coefficientX = Double.parseDouble(matcher.group(1));
            double constant = Double.parseDouble(matcher.group(2));

            return new Double[]{coefficientX, constant};
        }

        return new Double[]{0.0};
    }

    public static Map<Integer, Double[]> extractQuadraticCoefficients(Map<Integer, String> data) {
        Map<Integer, Double[]> result = new HashMap<>();

        for (Map.Entry<Integer, String> entry : data.entrySet()) {
            Integer entryKey = entry.getKey();
            String entryValue = entry.getValue();
            Double[] coefficients = extractQuadraticCoefficients(entryValue);
            result.put(entryKey, coefficients);
        }

        return result;
    }

    private static Double[] extractQuadraticCoefficients(String expression) {
        Pattern pattern = Pattern.compile("\\((-?\\d*\\.?\\d*)\\)\\*x\\^2\\+\\((-?\\d*\\.?\\d*)\\)\\*x\\+\\((-?\\d*\\.?\\d*)\\)");
        Matcher matcher = pattern.matcher(expression);

        if (matcher.matches()) {
            double coefficientX2 = Double.parseDouble(matcher.group(1));
            double coefficientX = Double.parseDouble(matcher.group(2));
            double constant = Double.parseDouble(matcher.group(3));

            return new Double[]{coefficientX2, coefficientX, constant};
        }

        return new Double[]{0.0};
    }

    private Integer getSelectedIndex(TableView<?> tableView) {
        if (!tableView.getSelectionModel().isEmpty()) {
            return tableView.getSelectionModel().getSelectedIndex();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Предупреждение");
            alert.setHeaderText(null);
            alert.setContentText("Стержень не выбран. Выберите стержень в таблице кликом по нему.");
            alert.showAndWait();
            return null;
        }
    }

    public void calculateMovingInPoint() {
        String inputXText = inputXMoving.getText();
        if (Objects.nonNull(startPoints)) {
            try {
                double x = Double.parseDouble(inputXText);
                int index = 0;
                for (int i = 1; i < startPoints.size(); i++) {
                    double startPoint = startPoints.get(i);
                    double endPoint = endPoints.get(i);
                    if (x <= endPoint && x >= startPoint) {
                        index = i;
                        break;
                    }
                }
                if (index > 0) {
                    List<Double> value = calculateMoving(index, List.of(x));
                    createTable("Перемещения в точке " + round(x),
                            List.of(x), "Ux", value, false, 0);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Предупреждение");
                    alert.setHeaderText(null);
                    alert.setContentText(String.format("Значение Х не входит в конструкцию [%s,%s]",
                            startPoints.get(1), endPoints.get(endPoints.size() - 1)));
                    alert.showAndWait();
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.setContentText("Значение Х имеет не верный формат.");
                alert.showAndWait();
            }
        }
    }

    public void calculateStrongInPoint() {
        String inputXText = inputXStrong.getText();
        if (Objects.nonNull(startPoints)) {
            try {
                double x = Double.parseDouble(inputXText);
                int index = 0;
                for (int i = 1; i < startPoints.size(); i++) {
                    double startPoint = startPoints.get(i);
                    double endPoint = endPoints.get(i);
                    if (x <= endPoint && x >= startPoint) {
                        index = i;
                        break;
                    }
                }
                if (index > 0) {
                    List<Double> value = calculateStrong(index, List.of(x));
                    createTable("Продольные силы в точке " + round(x),
                            List.of(x), "Nx", value, false, 0);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Предупреждение");
                    alert.setHeaderText(null);
                    alert.setContentText(String.format("Значение Х не входит в конструкцию [%s,%s]",
                            startPoints.get(1), endPoints.get(endPoints.size() - 1)));
                    alert.showAndWait();
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Предупреждение");
                alert.setHeaderText(null);
                alert.setContentText("Значение Х имеет не верный формат.");
                alert.showAndWait();
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
