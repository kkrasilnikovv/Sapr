package kkrasilnikovv.processor;

import kkrasilnikovv.postprocessor.LongitudinalStrongData;
import kkrasilnikovv.postprocessor.MovingData;
import kkrasilnikovv.postprocessor.NormalVoltageData;
import kkrasilnikovv.preprocessor.model.Beam;
import kkrasilnikovv.preprocessor.model.DataFile;
import kkrasilnikovv.preprocessor.model.Point;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.math3.linear.MatrixUtils.createRealMatrix;

public class Calculators {

    public CalculationFile calculate(DataFile dataFile) {
        List<Beam> beams = dataFile.getBeamList();
        List<Point> points = dataFile.getPointList();
        int nodeCount = points.size();
        int barCount = nodeCount - 1;
        List<Double> elasticMods = beams.stream().map(Beam::getElasticity).toList();
        List<Double> areas = beams.stream().map(Beam::getSquare).toList();
        List<Double> lengths = beams.stream().map(Beam::getLength).toList();
        double[] nodeLoads = points.stream().mapToDouble(Point::getFx).toArray();
        double[] barLoads = beams.stream().mapToDouble(Beam::getStrongQ).toArray();
        double[][] reactionVectorData = new double[nodeCount][1];
        double[][] reactionMatrixData = new double[nodeCount][nodeCount];
        for (int i = 0; i < nodeCount; i++) {
            for (int j = 0; j < nodeCount; j++) {
                if (i == j && i > 0 && i < barCount) {
                    reactionMatrixData[i][j] = (elasticMods.get(i - 1) * areas.get(i - 1)) / lengths.get(i - 1) + (elasticMods.get(j) * areas.get(j)) / lengths.get(j);
                } else if (i == j + 1) {
                    reactionMatrixData[i][j] = -(elasticMods.get(j) * areas.get(j)) / lengths.get(j);
                } else if (j == i + 1) {
                    reactionMatrixData[i][j] = -(elasticMods.get(i) * areas.get(i)) / lengths.get(i);
                } else {
                    reactionMatrixData[i][j] = .0;
                }
            }
        }
        for (int idx = 1; idx < barCount; idx++) {
            reactionVectorData[idx][0] = nodeLoads[idx] + barLoads[idx] * lengths.get(idx) / 2 + barLoads[idx - 1] * lengths.get(idx - 1) / 2;
        }
        if (dataFile.isSupportOnLeft()) {
            reactionMatrixData[0][0] = 1.0;
            reactionMatrixData[0][1] = 0.0;
            reactionMatrixData[1][0] = 0.0;
            reactionVectorData[0][0] = 0.0;
        } else {
            reactionMatrixData[0][0] = (elasticMods.get(0) * areas.get(0)) / lengths.get(0);
            reactionVectorData[0][0] = nodeLoads[0] + barLoads[0] * lengths.get(0) / 2;
        }
        if (dataFile.isSupportOnRight()) {
            reactionMatrixData[barCount][barCount] = 1.0;
            reactionMatrixData[barCount - 1][barCount] = 0.0;
            reactionMatrixData[barCount][barCount - 1] = 0.0;
            reactionVectorData[barCount][0] = 0.0;
        } else {
            reactionMatrixData[barCount][barCount] = (elasticMods.get(barCount - 1) * areas.get(barCount - 1)) / lengths.get(barCount - 1);
            reactionVectorData[barCount][0] = nodeLoads[barCount] + barLoads[barCount - 1] * lengths.get(barCount - 1) / 2;
        }
        double[] uZeros = new double[barCount];
        double[] uLengths = new double[barCount];
        RealMatrix deltaVector = createDeltaMatrix(reactionMatrixData, reactionVectorData);
        for (int idx = 0; idx < barCount; idx++) {
            uZeros[idx] = deltaVector.getEntry(idx, 0);
        }
        System.arraycopy(uZeros, 1, uLengths, 0, barCount - 1);
        uLengths[barCount - 1] = deltaVector.getEntry(barCount, 0);
        Calculator.CalculatorBuilder calculatorBuilder = Calculator.builder();
        for (int idx = 0; idx < barCount; idx++) {
            double elasticity = elasticMods.get(idx);
            double area = areas.get(idx);
            double length = lengths.get(idx);
            double nxb = calculateNxb(elasticity, area, length, uZeros[idx], uLengths[idx], barLoads[idx]);
            double uxa = calculateUxa(elasticity, area, barLoads[idx]);
            double uxb = calculateUxb(elasticity, area, length, uZeros[idx], uLengths[idx], barLoads[idx]);
            calculatorBuilder.addMovementCalculation(new MovementCalculation(-barLoads[idx] / areas.get(idx), nxb / areas.get(idx)));
            calculatorBuilder.addNormalVoltageCalculation(new NormalVoltageCalculation(uxa, uxb, uZeros[idx]));
            calculatorBuilder.addLongitudinalForcesCalculation(new LongitudinalForceCalculation(-barLoads[idx], nxb));
        }
        return calculateWithLength(dataFile, calculatorBuilder.build().getStringRepresentation());
    }

    public CalculationFile calculateWithLength(DataFile dataFile, List<Map<Integer, Double[]>> objects) {
        return new CalculationFile(calculateNormalVoltage(dataFile.getBeamList(), objects.get(0)),
                calculateLongitudinalStrong(dataFile.getBeamList(), objects.get(2)),
                calculateMoving(dataFile.getBeamList(), objects.get(1)));
    }

    private Map<Integer, List<NormalVoltageData>> calculateNormalVoltage(List<Beam> beams, Map<Integer, Double[]> normalVoltage) {
        Map<Integer, List<NormalVoltageData>> normalVoltages = new HashMap<>();
        for (Beam beam : beams) {
            List<NormalVoltageData> normalVoltageDataList = new ArrayList<>();
            Integer id = beam.getId().get();
            Double[] value = normalVoltage.get(id);
            double[] x = divideSegment(beam.getX1(), beam.getX2());
            for (double d : x) {
                normalVoltageDataList.add(new NormalVoltageData(d, value));
            }
            normalVoltages.put(id, normalVoltageDataList);
        }
        return normalVoltages;
    }

    private Map<Integer, List<MovingData>> calculateMoving(List<Beam> beams, Map<Integer, Double[]> moving) {
        Map<Integer, List<MovingData>> movings = new HashMap<>();
        for (Beam beam : beams) {
            List<MovingData> movingDataList = new ArrayList<>();
            Integer id = beam.getId().get();
            Double[] value = moving.get(id);
            double[] x = divideSegment(beam.getX1(), beam.getX2());
            for (double d : x) {
                movingDataList.add(new MovingData(d, value));
            }
            movings.put(id, movingDataList);
        }
        return movings;
    }

    private Map<Integer, List<LongitudinalStrongData>> calculateLongitudinalStrong(List<Beam> beams, Map<Integer, Double[]> longitudinalStrong) {
        Map<Integer, List<LongitudinalStrongData>> longitudinalStrongs = new HashMap<>();
        for (Beam beam : beams) {
            List<LongitudinalStrongData> longitudinalStrongData = new ArrayList<>();
            Integer id = beam.getId().get();
            Double[] value = longitudinalStrong.get(id);
            double[] x = divideSegment(beam.getX1(), beam.getX2());
            for (double d : x) {
                longitudinalStrongData.add(new LongitudinalStrongData(d, value));
            }
            longitudinalStrongs.put(id, longitudinalStrongData);
        }
        return longitudinalStrongs;
    }

    private double[] divideSegment(double start, double end) {
        double[] points = new double[10];
        double delta = (end - start) / (10 - 1);

        for (int i = 0; i < 10; i++) {
            points[i] = start + i * delta;
        }

        return points;
    }

    private RealMatrix createDeltaMatrix(double[][] reactionMatrixData, double[][] reactionVectorData) {
        RealMatrix reactionMatrix = createRealMatrix(reactionMatrixData);
        RealMatrix reactionVector = createRealMatrix(reactionVectorData);
        RealMatrix inverseReactionMatrix = new LUDecomposition(reactionMatrix).getSolver().getInverse();
        return inverseReactionMatrix.multiply(reactionVector);
    }

    private double calculateNxb(double elasticMod, double area, double length, double Up0, double UpL, double q) {
        return (elasticMod * area / length) * (UpL - Up0) + q * length / 2;
    }

    private double calculateUxb(double E, double A, double L, double Up0, double UpL, double q) {
        return (UpL - Up0 + (q * L * L) / (2 * E * A)) / L;
    }

    private double calculateUxa(double E, double A, double q) {
        return -q / (2 * E * A);
    }
}