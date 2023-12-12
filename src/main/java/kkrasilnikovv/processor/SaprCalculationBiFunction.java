package kkrasilnikovv.processor;

import java.util.function.BiFunction;

public interface SaprCalculationBiFunction extends BiFunction<Double, Integer, String> {
    String representation();
}
