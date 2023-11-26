package kkrasilnikovv.preprocessor.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
public class Beam implements Serializable {
    private final SimpleIntegerProperty id;
    private final SimpleIntegerProperty startPoint;
    private final SimpleIntegerProperty endPoint;
    private final SimpleDoubleProperty square;
    private final SimpleIntegerProperty strongQ;
    private final SimpleDoubleProperty elasticity;
    private final SimpleDoubleProperty tension;
    private double x1;
    private double x2;
    private double coefficient;
    private double coefficientLength;
    private double length;

    public Beam(int id, int startPoint, int endPoint, double square, int strongQ, double elasticity, double tension) {
        this.id = new SimpleIntegerProperty(id);
        this.startPoint = new SimpleIntegerProperty(startPoint);
        this.endPoint = new SimpleIntegerProperty(endPoint);
        this.square = new SimpleDoubleProperty(square);
        this.strongQ = new SimpleIntegerProperty(strongQ);
        this.elasticity = new SimpleDoubleProperty(elasticity);
        this.tension = new SimpleDoubleProperty(tension);
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public int getStartPoint() {
        return startPoint.get();
    }

    public void setLength() {
        this.length = this.x2 - this.x1;
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

    public double getSquare() {
        return square.get();
    }

    public SimpleDoubleProperty squareProperty() {
        return square;
    }

    public double getElasticity() {
        return elasticity.get();
    }

    public SimpleDoubleProperty elasticityProperty() {
        return elasticity;
    }

    public double getTension() {
        return tension.get();
    }

    public SimpleDoubleProperty tensionProperty() {
        return tension;
    }

    public void setSquare(double square) {
        this.square.set(square);
    }

    public void setTension(double tension) {
        this.tension.set(tension);
    }

    public void setElasticity(double elasticity) {
        this.elasticity.set(elasticity);
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