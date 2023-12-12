package kkrasilnikovv.processor;


// Калькулятор перемещений (σx)
public class MovementCalculation implements SaprCalculationBiFunction {
    private final double firstArg;
    private final double secondArg;

    public MovementCalculation(double firstArg, double secondArg) {
        this.firstArg = firstArg;
        this.secondArg = secondArg;
    }

    @Override
    public String apply(Double x, Integer precision) {
        return String.format(firstArg +"*x+" + secondArg);
    }

    @Override
    public String representation() {
        return String.format(firstArg +"*x+" + secondArg);
    }

    @Override
    public String toString() {
        return "MovementCalculation{" +
                "firstArg=" + firstArg +
                ", secondArg=" + secondArg +
                '}';
    }
}
