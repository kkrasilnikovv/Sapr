package kkrasilnikovv.processor;


// Калькулятор нормальных напряжений (Ux)
public class NormalVoltageCalculation implements SaprCalculationBiFunction {

    private final double firstArg;
    private final double secondArg;
    private final double thirdArg;

    public NormalVoltageCalculation(double firstArg, double secondArg, double thirdArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
        this.thirdArg = thirdArg;
    }

    @Override
    public String apply(Double x, Integer integer) {
        return String.format("(" + firstArg + ")" + "*x^2+" + "(" + secondArg + ")" + "*x+" + "(" + thirdArg + ")");
    }

    @Override
    public String representation() {
        return String.format("(" + firstArg + ")" + "*x^2+" + "(" + secondArg + ")" + "*x+" + "(" + thirdArg + ")");
    }

    @Override
    public String toString() {
        return "NormalVoltageCalculation{" +
                "firstArg=" + firstArg +
                ", secondArg=" + secondArg +
                ", thirdArg=" + thirdArg +
                '}';
    }
}
