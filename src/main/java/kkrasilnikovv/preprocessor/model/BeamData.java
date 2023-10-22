package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@AllArgsConstructor
@Getter
@Setter
public class BeamData implements Serializable {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty startPoint;
    private final SimpleIntegerProperty endPoint;
    public BeamData(int id, int startPoint, int endPoint) {
        this.id = new SimpleIntegerProperty(id);
        this.startPoint = new SimpleIntegerProperty(startPoint);
        this.endPoint = new SimpleIntegerProperty(endPoint);
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

    public int getStartPoint() {
        return startPoint.get();
    }

    public SimpleIntegerProperty startPointProperty() {
        return startPoint;
    }

    public void setStartPoint(int startPoint) {
        this.startPoint.set(startPoint);
    }

    public int getEndPoint() {
        return endPoint.get();
    }

    public SimpleIntegerProperty endPointProperty() {
        return endPoint;
    }

    public void setEndPoint(int endPoint) {
        this.endPoint.set(endPoint);
    }
}

