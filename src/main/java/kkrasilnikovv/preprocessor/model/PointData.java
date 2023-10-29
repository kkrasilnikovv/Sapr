package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;

public class PointData {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty fx;

    public PointData(int id, int fx) {
        this.id = new SimpleIntegerProperty(id);
        this.fx = new SimpleIntegerProperty(fx);
    }

    public int getId() {
        return id.get();
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
}
