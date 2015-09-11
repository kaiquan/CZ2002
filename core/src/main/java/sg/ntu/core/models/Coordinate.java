package sg.ntu.core.models;

/**
 * Created by Moistyburger on 7/9/15.
 */
public class Coordinate {

    private double lat;
    private double lon;

    public Coordinate(double lat, double lon){
        setLat(lat);
        setLon(lon);
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }



}
