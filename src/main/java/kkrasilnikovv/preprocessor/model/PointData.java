package kkrasilnikovv.preprocessor.model;

import com.google.gson.annotations.Expose;
import javafx.beans.property.SimpleIntegerProperty;

public class PointData {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty fx;
    private final SimpleIntegerProperty fy;

    public PointData(int id, int fx, int fy) {
        this.id = new SimpleIntegerProperty(id);
        this.fx = new SimpleIntegerProperty(fx);
        this.fy = new SimpleIntegerProperty(fy);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public int getFx() {
        return fx.get();
    }

    public SimpleIntegerProperty fxProperty() {
        return fx;
    }

    public void setFx(int fx) {
        this.fx.set(fx);
    }

    public int getFy() {
        return fy.get();
    }

    public SimpleIntegerProperty fyProperty() {
        return fy;
    }

    public void setFy(int fy) {
        this.fy.set(fy);
    }
}
