package kkrasilnikovv.postprocessor;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class MovingData implements Data{
    private final double value;
    private final double x;

    public MovingData(double x, Double[] value) {
        this.x = round(x);
        this.value = round(calculate(x, value));
    }

    private double calculate(double x, Double[] value) {
        return value[0] * x + value[1];
    }
    private double round(double value){
        return new BigDecimal(value).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }
}