package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    private final SimpleIntegerProperty width;
    private final SimpleStringProperty sectionType;

    public BeamData(int id, int startPoint, int endPoint, int width, String sectionType) {
        this.id = new SimpleIntegerProperty(id);
        this.startPoint = new SimpleIntegerProperty(startPoint);
        this.endPoint = new SimpleIntegerProperty(endPoint);
        this.width = new SimpleIntegerProperty(width);
        this.sectionType = new SimpleStringProperty(sectionType);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public int getStartPoint() {
        return startPoint.get();
    }

    public SimpleIntegerProperty startPointProperty() {
        return startPoint;
    }

    public int getEndPoint() {
        return endPoint.get();
    }

    public SimpleIntegerProperty endPointProperty() {
        return endPoint;
    }

    public int getWidth() {
        return width.get();
    }

    public SimpleIntegerProperty width() {
        return width;
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public String getSectionType() {
        return sectionType.get();
    }

    public SimpleStringProperty sectionType() {
        return sectionType;
    }

    public void setWidth(String sectionType) {
        this.sectionType.set(sectionType);
    }
}