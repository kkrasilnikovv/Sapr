package kkrasilnikovv.processor;


// Калькулятор продольных сил (Nx)
public class LongitudinalForceCalculation implements SaprCalculationBiFunction {
    private final double firstArg;
    private final double secondArg;

    public LongitudinalForceCalculation(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }

    @Override
    public String apply(Double x, Integer precision) {
        return String.format("(" + firstArg + ")" + "*x+" + "(" + secondArg + ")");
    }

    @Override
    public String representation() {
        return String.format("(" + firstArg + ")" + "*x+" + "(" + secondArg + ")");
    }

    @Override
    public String toString() {
        return "LongitudinalForceCalculation{" +
                "firstArg=" + firstArg +
                ", secondArg=" + secondArg +
                '}';
    }
}
