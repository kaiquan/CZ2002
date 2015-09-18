package sg.ntu.cz2002.entity;

import java.util.List;

/**
 * Created by Moistyburger on 7/9/15.
 */
public class Direction {
    private List<Coordinate> coordinateList;
    private List<String> description;

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<Coordinate> getCoordinateList() {
        return coordinateList;
    }

    public void setCoordinateList(List<Coordinate> coordinateList) {
        this.coordinateList = coordinateList;
    }


}
