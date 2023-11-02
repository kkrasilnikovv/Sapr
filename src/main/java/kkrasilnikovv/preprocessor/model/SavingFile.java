package kkrasilnikovv.preprocessor.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SavingFile {
    private List<PointData> pointList;
    private List<BeamData> beamList;
    private int lastIdPoint;
    private boolean supportOnLeft;
    private boolean supportOnRight;
}
