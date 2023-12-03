package kkrasilnikovv.postprocessor;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DataInTable {
    private int index;
    private List<Double> x;
    private List<Double> value;

    public boolean isEmpty() {
        return Objects.isNull(x) || Objects.isNull(value) || x.isEmpty() || value.isEmpty();
    }
}
