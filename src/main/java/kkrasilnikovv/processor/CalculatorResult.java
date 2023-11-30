package kkrasilnikovv.processor;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalculatorResult {
    private double movement;
    private double x;
    private double longitudinalForce;
    private double normalVoltage;

    public CalculatorResult() {
        this(0, 0, 0, 0);
    }

    public CalculatorResult(double x, double movement, double longitudinalForce, double normalVoltage) {
        this.x = x;
        this.movement = movement;
        this.longitudinalForce = longitudinalForce;
        this.normalVoltage = normalVoltage;
    }

    @Override
    public String toString() {
        return "CalculatorResult{" +
                "movement=" + movement +
                ", x=" + x +
                ", longitudinalForce=" + longitudinalForce +
                ", normalVoltage=" + normalVoltage +
                '}';
    }
}
