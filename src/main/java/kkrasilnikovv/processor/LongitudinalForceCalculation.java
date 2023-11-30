package kkrasilnikovv.processor;

import org.apache.commons.math3.util.Precision;

// Калькулятор продольных сил (Nx)
public class LongitudinalForceCalculation implements SaprCalculationBiFunction {
    private final double firstArg;
    private final double secondArg;

    public LongitudinalForceCalculation(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }

    @Override
    public Double apply(Double x, Integer precision) {
        double result = (firstArg * x) + secondArg;
        return Precision.round(result, precision);
    }

    @Override
    public Double[] representation() {
        return new Double[]{firstArg, secondArg};
    }

    @Override
    public String toString() {
        return "LongitudinalForceCalculation{" +
                "firstArg=" + firstArg +
                ", secondArg=" + secondArg +
                '}';
    }
}
