package kkrasilnikovv.processor;

import kkrasilnikovv.postprocessor.LongitudinalStrongData;
import kkrasilnikovv.postprocessor.MovingData;
import kkrasilnikovv.postprocessor.NormalVoltageData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CalculationFile {
    private Map<Integer, List<NormalVoltageData>> normalVoltage;
    private Map<Integer, List<LongitudinalStrongData>> longitudinalStrong;
    private Map<Integer, List<MovingData>> moving;
    public boolean isEmpty(){
        return Objects.isNull(normalVoltage) || Objects.isNull(longitudinalStrong) ||Objects.isNull(moving)
                || normalVoltage.isEmpty() || longitudinalStrong.isEmpty() ||moving.isEmpty();
    }
}
