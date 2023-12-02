package kkrasilnikovv.postprocessor;

import javafx.beans.property.SimpleIntegerProperty;

public class IntegerWrapper {
    private final SimpleIntegerProperty value;

    public IntegerWrapper(int value) {
        this.value = new SimpleIntegerProperty(value);
    }

    public SimpleIntegerProperty valueProperty() {
        return value;
    }

    public int getValue() {
        return value.get();
    }

    public void setValue(int value) {
        this.value.set(value);
    }
}