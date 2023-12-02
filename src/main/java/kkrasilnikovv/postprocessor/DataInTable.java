package kkrasilnikovv.postprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataInTable {
    private int index;
    private List<Double> x;
    private List<Double> value;
}
