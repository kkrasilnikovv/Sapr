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

    public boolean isEmpty() {
        return Objects.isNull(normalVoltage) || Objects.isNull(longitudinalStrong) || Objects.isNull(moving)
                || normalVoltage.isEmpty() || longitudinalStrong.isEmpty() || moving.isEmpty();
    }
}
