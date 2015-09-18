package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;

import sg.ntu.cz2002.Core;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Direction;
import sg.ntu.cz2002.entity.Location;
import sg.ntu.cz2002.entity.Weather;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class MainActivity extends Activity implements LocationListener {

    private int x=0;
    private LocationManager locationManager;
    private android.location.Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Core.context=getApplicationContext();
        init();
    }


    //THIS IS THE INITIAL METHODS FOR UR R.id MAPPING AND STARTING GPS METHOD
    public void init(){
        //add all the R.id statements here


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //CHECKS IF GPS IS ENABLED
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
            Log.i("GPS","GPS NOT ENABLED");
            //SHOW THE BLOCKER
        }
        else{
            //START THE GPS
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }

//        Coordinate from= new Coordinate( 18304.68,36152.73);
//        Coordinate destination= new Coordinate(21591.48,33095.24);
//        getDirectionData(from,destination);
    }

    //===================================//
    //         UI LOGIC METHODS          //
    //===================================//

    public void showGPSBlocker(){

    }
    public void hideGPSBlocker(){

    }
    //....etc etc


    //===================================//
    //    API IMPLEMENTATION METHODS     //
    //===================================//

    //THIS IS THE METHOD TO CALL THE GET DIRECTION DATA;
    //PARAMATERS ARE THE CURRENT AND DESTINATION COORDINATE OBJECT
    public void getDirectionData(Coordinate from, Coordinate destination){
        Core.getInstance().getDirectionAPI().getDirection(from, destination, new Core.Callback() {
            @Override
            public void success(Object o, JSONObject response) {
                Direction d = (Direction) o;
                for (int i = 0; i < d.getCoordinateList().size(); i++) {
                    Log.i("Direction api output", d.getCoordinateList().get(i).getLat() + "," + d.getCoordinateList().get(i).getLon());
                }
            }

            @Override
            public void failure(String error) {

            }
        });
    }

    //THIS IS THE METHOD TO CALL THE WEATHER DATA
    public void getWeatherData(){
        Core.getInstance().getWeatherAPI().getWeatherData(new Core.Callback() {
            @Override
            public void success(Object weathers, JSONObject response) {
                Weather weather= (Weather)((ArrayList) weathers).get(0);
                Log.i("SUCCESS WEATHER API CALL",weather.getAreaName() );
                //CACULATE THE NEAREST WEATHER RESULT USING ONEMAP API
            }
            @Override
            public void failure(String error) {
                Log.i("FAILURE WEATHER API CALL",error);
            }
        });
    }

    //THIS IS THE METHOD TO GET THE LOCATIONS FROM THE SELECTED CATEGORY
    public void getLocationsData(){
        x=0;
        final Location.Category[] categories= new Location.Category[3];
        categories[0]= Location.Category.Parks;
        categories[1]= Location.Category.HawkerCentres;
        categories[2]= Location.Category.Libraries;
        Core.getInstance().getLocationAPI().getLocationsFromCategories(categories, new Core.Callback() {
            @Override
            public void success(Object locations, JSONObject response) {
                ArrayList<Location> location= ((ArrayList) locations);
                //for some reason it starts from index 1
                x++;
                Log.i("categories count",x+"");
                if(x==categories.length){
                    //start picking the random one base on distance from gps
                    Log.i("SUCCESS location API CALL",location.get(1).getName().toString());
                    Log.i("SUCCESS location hawker API CALL",location.get(location.size()-1).getName().toString());
                }

            }

            @Override
            public void failure(String error) {

            }
        });
    }

    //THIS IS THE METHOD TO GT THE CATEGORY BASE ON THE WEATHER, TRUE=GOOD, FALSE=BAD
    public void getCategoriesData(){
        ArrayList<Location.Category> categories = Core.getInstance().getCategoryAPI().getCategoriesOptions(true);
    }


    //===================================//
    //GPS LISTENER IMPLEMENTATION METHODS//
    //===================================//
    @Override
    public void onLocationChanged(android.location.Location location) {
        currentLocation = location;
        Log.i("CURRENT LOCATION",location.toString());
         //ONCE WE GET THE LOCATION WE STOP GPS AND CALL GET WEATHER API
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //cant remember the status meaning need to check it out on ur own
    }

    @Override
    public void onProviderEnabled(String provider) {
        //HIDE GPS BLOCKER
    }

    @Override
    public void onProviderDisabled(String provider) {
        //SHOW GPS BLOCKER
    }
}
