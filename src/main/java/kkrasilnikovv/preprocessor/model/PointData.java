package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;

public class PointData {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty fx;
    private final SimpleIntegerProperty strongF;

    public PointData(int id, int fx, int strongF) {
        this.id = new SimpleIntegerProperty(id);
        this.fx = new SimpleIntegerProperty(fx);
        this.strongF = new SimpleIntegerProperty(strongF);
    }

    public int getId() {
        return id.get();
    }

    public int getFx() {
        return fx.get();
    }
    public int getStrong(){
        return strongF.get();
    }
    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public SimpleIntegerProperty fxProperty() {
        return fx;
    }

    public void setFx(int fx) {
        this.fx.set(fx);
    }

    public SimpleIntegerProperty StrongFProperty() {
        return strongF;
    }

    public void setStrongF(int strongF) {
        this.strongF.set(strongF);
    }
}
