package kkrasilnikovv.preprocessor.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@AllArgsConstructor
@Getter
public class SavingFile {
    private List<PointData> pointList;
    private List<BeamData> beamList;
    private int lastIdPoint;
    private boolean supportOnLeft;
    private boolean supportOnRight;
}
