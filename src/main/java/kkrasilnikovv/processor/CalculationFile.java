package kkrasilnikovv.processor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CalculationFile {
    private Map<Integer,Double> normalVoltage;
    private Map<Integer,Double> longitudinalStrong;
    private Map<Integer,Double> moving;
}
