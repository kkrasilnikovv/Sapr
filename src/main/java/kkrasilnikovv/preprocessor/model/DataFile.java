package kkrasilnikovv.preprocessor.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Getter
public class DataFile {
    private List<Point> pointList;
    private List<Beam> beamList;
    private int lastIdPoint;
    private boolean supportOnLeft;
    private boolean supportOnRight;

    public DataFile() {
        pointList = new ArrayList<>();
        beamList = new ArrayList<>();
        lastIdPoint = 0;
        supportOnLeft = false;
        supportOnRight = false;
    }
    public boolean isEmpty(){
        return pointList.isEmpty()&&beamList.isEmpty();
    }
}
