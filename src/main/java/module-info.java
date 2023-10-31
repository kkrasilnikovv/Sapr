module kkrasilnikovv {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.google.gson;
    exports kkrasilnikovv.main;
    exports kkrasilnikovv.preprocessor;
    opens kkrasilnikovv.main;
    opens kkrasilnikovv.preprocessor;
    opens kkrasilnikovv.preprocessor.controller;
    opens kkrasilnikovv.preprocessor.model;
    exports kkrasilnikovv.preprocessor.prorepty_adapter;
    opens kkrasilnikovv.preprocessor.prorepty_adapter;
}