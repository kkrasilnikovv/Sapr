package kkrasilnikovv.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CalculationFile {
    private Map<Integer, String> normalVoltage;
    private Map<Integer, String> longitudinalStrong;
    private Map<Integer, String> moving;
    private Map<Integer, Double> predelVoltage;
    private Map<Integer, Double> startPoint;
    private Map<Integer, Double> endPoint;
    public CalculationFile(Map<Integer, String> normalVoltage, Map<Integer, String> longitudinalStrong, Map<Integer, String> moving) {
        this.normalVoltage = normalVoltage;
        this.longitudinalStrong = longitudinalStrong;
        this.moving = moving;
    }

    public boolean isEmpty() {
        return Objects.isNull(normalVoltage) || Objects.isNull(longitudinalStrong) || Objects.isNull(moving)
                || normalVoltage.isEmpty() || longitudinalStrong.isEmpty() || moving.isEmpty();
    }
}
