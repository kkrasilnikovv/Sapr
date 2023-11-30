package kkrasilnikovv.processor;

import java.util.function.BiFunction;

public interface SaprCalculationBiFunction extends BiFunction<Double, Integer, Double> {
    Double[] representation();
}
