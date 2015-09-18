package sg.ntu.cz2002.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;

import sg.ntu.cz2002.Core;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Direction;
import sg.ntu.cz2002.entity.Location;
import sg.ntu.cz2002.entity.Weather;


public class MainActivity extends AppCompatActivity {

    private int x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Core.context=getApplicationContext();
        init();
    }


    public void init(){
        //add all the R.files here
        Coordinate from= new Coordinate( 18304.68,36152.73);
        Coordinate destination= new Coordinate(21591.48,33095.24);

        getDirectionData(from,destination);
    }

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
    public void getWeatherData(){
        //sample api call for get weather data
        Core.getInstance().getWeatherAPI().getWeatherData(new Core.Callback() {
            @Override
            public void success(Object weathers, JSONObject response) {
                Weather weather= (Weather)((ArrayList) weathers).get(0);
                Log.i("SUCCESS WEATHER API CALL",weather.getAreaName() );
            }
            @Override
            public void failure(String error) {
                Log.i("FAILURE WEATHER API CALL",error);
            }
        });
    }
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
    public void getCategoriesData(){

    }


}
