package kkrasilnikovv.preprocessor.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;


@AllArgsConstructor
@Getter
public class DataFile {
    private List<Point> pointList;
    private List<Beam> beamList;
    private int lastIdPoint;
    private boolean supportOnLeft;
    private boolean supportOnRight;

    public boolean isEmpty() {
        return (Objects.isNull(pointList) || Objects.isNull(beamList)) || (pointList.isEmpty() && beamList.isEmpty());
    }
}
