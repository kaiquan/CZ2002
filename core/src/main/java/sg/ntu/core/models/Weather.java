package sg.ntu.core.models;

/**
 * Created by Moistyburger on 25/8/15.
 */
public class Weather {


    public enum WeatherType{


        FairDAY,
        FairNIGHT,
        PartlyCloudy,
        Cloudy,
        Hazy,
        Windy,
        Rain,
        PassingShowers,
        Showers,
        ThunderyShowers;
    }
    public enum AreaZone{
        C,
        N,
        S,
        E,
        W;
    }
    public enum WeatherIcon{
        FD,
        FN,
        PC,
        CD,
        HZ,
        WD,
        RA,
        PS,
        SH,
        TS;
    }

    private String issued_dateTime;
    private String areaName;
    private Coordinate areaCoordinate;
    private WeatherType areaForecast;
    private AreaZone areaZone;
    private WeatherIcon icon;

    public WeatherIcon getIcon() {
        return icon;
    }

    public void setIcon(WeatherIcon icon) {
        this.icon = icon;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getIssued_dateTime() {
        return issued_dateTime;
    }

    public void setIssued_dateTime(String issued_dateTime) {
        this.issued_dateTime = issued_dateTime;
    }

    public Coordinate getAreaCoordinate() {
        return areaCoordinate;
    }

    public void setAreaCoordinate(Coordinate areaCoordinate) {
        this.areaCoordinate = areaCoordinate;
    }

    public WeatherType getAreaForecast() {
        return areaForecast;
    }

    public void setAreaForecast(WeatherType areaForecast) {
        this.areaForecast = areaForecast;
    }

    public AreaZone getAreaZone() {
        return areaZone;
    }

    public void setAreaZone(AreaZone areaZone) {
        this.areaZone = areaZone;
    }




}
