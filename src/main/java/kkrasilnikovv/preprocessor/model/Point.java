package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Point {
    private final SimpleIntegerProperty id;
    private final SimpleDoubleProperty fx;
    private final SimpleIntegerProperty strongF;

    public Point(int id, double fx, int strongF) {
        this.id = new SimpleIntegerProperty(id);
        this.fx = new SimpleDoubleProperty(fx);
        this.strongF = new SimpleIntegerProperty(strongF);
    }

    public int getId() {
        return id.get();
    }

    public double getFx() {
        return fx.get();
    }

    public int getStrong() {
        return strongF.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleDoubleProperty fxProperty() {
        return fx;
    }

    public void setFx(double fx) {
        this.fx.set(fx);
    }

    public SimpleIntegerProperty StrongFProperty() {
        return strongF;
    }

    public void setStrongF(int strongF) {
        this.strongF.set(strongF);
    }
}
