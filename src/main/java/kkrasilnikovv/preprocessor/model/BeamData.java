package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class BeamData implements Serializable {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty startPoint;
    private final SimpleIntegerProperty endPoint;
    private final SimpleIntegerProperty width;
    private final SimpleStringProperty sectionType;
    private final SimpleIntegerProperty strongQ;
    private int x1;
    private int x2;

    public BeamData(int id, int startPoint, int endPoint, int width, String sectionType, int strongQ) {
        this.id = new SimpleIntegerProperty(id);
        this.startPoint = new SimpleIntegerProperty(startPoint);
        this.endPoint = new SimpleIntegerProperty(endPoint);
        this.width = new SimpleIntegerProperty(width);
        this.sectionType = new SimpleStringProperty(sectionType);
        this.strongQ = new SimpleIntegerProperty(strongQ);
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

    public SimpleIntegerProperty widthProperty() {
        return width;
    }

    public void setWidth(int width) {
        this.width.set(width);
    }

    public SimpleStringProperty sectionTypeProperty() {
        return sectionType;
    }

    public String getSectionType() {
        return sectionType.get();
    }

    public int getStrongQ() {
        if (Objects.isNull(strongQ)) {
            return 0;
        } else {
            return strongQ.get();
        }
    }

    public SimpleIntegerProperty strongQProperty() {
        return strongQ;
    }

}