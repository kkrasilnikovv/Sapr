package kkrasilnikovv.processor;

import java.util.*;

public class Calculator {
    private final List<SaprCalculationBiFunction> movementsCalculations;
    private final List<SaprCalculationBiFunction> normalVoltageCalculations;
    private final List<SaprCalculationBiFunction> longitudinalForceCalculations;

    public Calculator(List<SaprCalculationBiFunction> movementsCalculations,
                      List<SaprCalculationBiFunction> normalVoltageCalculations,
                      List<SaprCalculationBiFunction> longitudinalForceCalculations) {
        this.movementsCalculations = movementsCalculations;
        this.normalVoltageCalculations = normalVoltageCalculations;
        this.longitudinalForceCalculations = longitudinalForceCalculations;
    }

    public CalculationFile getStringRepresentation() {
        Map<Integer, Double[]> normalVoltage = new HashMap<>();
        Map<Integer, Double[]> longitudinalStrong = new HashMap<>();
        Map<Integer, Double[]> moving = new HashMap<>();

        for (int barIndex = 0; barIndex < movementsCalculations.size(); barIndex++) {
            normalVoltage.put(barIndex + 1, normalVoltageCalculations.get(barIndex).representation());
            longitudinalStrong.put(barIndex + 1, longitudinalForceCalculations.get(barIndex).representation());
            moving.put(barIndex + 1, movementsCalculations.get(barIndex).representation());
        }
        return new CalculationFile(normalVoltage, longitudinalStrong, moving);
    }

    @Override
    public String toString() {
        return "Calculator{" +
                "movementsCalculations=" + movementsCalculations +
                ", normalVoltageCalculations=" + normalVoltageCalculations +
                ", longitudinalForceCalculations=" + longitudinalForceCalculations +
                '}';
    }

    public static CalculatorBuilder builder() {
        return new CalculatorBuilder();
    }

    public static class CalculatorBuilder {
        private final List<SaprCalculationBiFunction> movementsCalculations = new ArrayList<>();
        private final List<SaprCalculationBiFunction> normalVoltageCalculations = new ArrayList<>();
        private final List<SaprCalculationBiFunction> longitudinalForceCalculations = new ArrayList<>();

        public void addMovementCalculation(SaprCalculationBiFunction movementCalculation) {
            movementsCalculations.add(movementCalculation);
        }

        public void addNormalVoltageCalculation(SaprCalculationBiFunction normalVoltageCalculation) {
            normalVoltageCalculations.add(normalVoltageCalculation);
        }

        public void addLongitudinalForcesCalculation(SaprCalculationBiFunction longitudinalForcesCalculation) {
            longitudinalForceCalculations.add(longitudinalForcesCalculation);
        }

        public Calculator build() {
            if (movementsCalculations.isEmpty()) {
                throw new IllegalStateException("Movements calculations can't be empty");
            }
            if (normalVoltageCalculations.isEmpty()) {
                throw new IllegalStateException("Normal Voltage calculations can't be empty");
            }
            if (longitudinalForceCalculations.isEmpty()) {
                throw new IllegalStateException("Longitudinal Force calculation can't be empty");
            }
            if (longitudinalForceCalculations.size() != movementsCalculations.size() ||
                    movementsCalculations.size() != normalVoltageCalculations.size()) {
                throw new IllegalStateException("Calculations size not equals");
            }
            return new Calculator(movementsCalculations, normalVoltageCalculations, longitudinalForceCalculations);
        }
    }
}
