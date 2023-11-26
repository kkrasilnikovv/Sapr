module kkrasilnikovv {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.google.gson;
    requires javafx.swing;
    opens kkrasilnikovv.main;
    opens kkrasilnikovv.preprocessor;
    opens kkrasilnikovv.preprocessor.controller;
    opens kkrasilnikovv.preprocessor.model;
    opens kkrasilnikovv.preprocessor.prorepty_adapter;
    opens kkrasilnikovv.processor;
    opens kkrasilnikovv.postprocessor;
    exports kkrasilnikovv.preprocessor.prorepty_adapter;
    exports kkrasilnikovv.main;
    exports kkrasilnikovv.preprocessor;
    exports kkrasilnikovv.preprocessor.model;
    exports kkrasilnikovv.processor;
    exports kkrasilnikovv.postprocessor;
}