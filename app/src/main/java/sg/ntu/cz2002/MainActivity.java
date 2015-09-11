package sg.ntu.cz2002;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONObject;
import java.util.ArrayList;
import sg.ntu.core.Core;
import sg.ntu.core.models.Location;
import sg.ntu.core.models.Weather;
import sg.ntu.core.util.PreferencesHelper;


public class MainActivity extends Activity {

    private PreferencesHelper mSharedPrefs;
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
