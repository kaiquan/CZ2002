package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
//import com.nineoldandroids.view.ViewHelper;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.ntu.cz2002.adapter.CategoryAdapter;
import sg.ntu.cz2002.controller.APIController;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.controller.Callback;
import sg.ntu.cz2002.controller.CategoryAPI;
import sg.ntu.cz2002.controller.DirectionAPI;
import sg.ntu.cz2002.controller.LocationsAPI;
import sg.ntu.cz2002.controller.WeatherAPI;
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
    private AlertDialog GPSBlocker = null;
    private int REQUEST_CODE;
    private CategoryAdapter categoryAdapter;
    private MapView mMapView = null;

    private Button mGoBtn;

    private ScrollView mScrollview;
    private ProgressDialog progress;
    private SeekBar mSeekBar;

    private ImageView mWeatherIcon;
    private TextView mWeatherCondition;

    private LinearLayout hawkerLayout, libraryLayout, museumLayout, parkLayout, waterVentureLayout, touristAttractionsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIController.context=getApplicationContext();
        //
        Intent intent = new Intent(this, DirectionActivity.class);
        intent.putExtra("DIRECTION_FROM", "1.1");
        intent.putExtra("DIRECTION_TO", "0.0");
       // startActivity(intent);
        init();
    }

    //THIS IS THE INITIAL METHODS FOR UR R.id MAPPING AND STARTING GPS
    public void init(){
        mScrollview = (ScrollView)findViewById(R.id.scrollview);
        mSeekBar = (SeekBar)findViewById(R.id.seekbar);
        mGoBtn = (Button)findViewById(R.id.goBtn);
        mWeatherCondition = (TextView)findViewById(R.id.weatherconditionTxt);
        mWeatherIcon = (ImageView)findViewById(R.id.weathericon);


        //the method for go Btn
        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check that at least one category is selected
                //than call the getlocatins method
            }
        });

        //starts the map view
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.enableWrapAround(true);
        ArcGISRuntime.setClientId("j9r0J2JIy8FFFfB8");
        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://www.onemap.sg/ArcGIS/rest/services/BASEMAP/MapServer"));
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {

            public void onStatusChanged(Object source, STATUS status) {
                if (source == mMapView && status == STATUS.INITIALIZED) {
                    LocationDisplayManager ls = mMapView.getLocationDisplayManager();
                    ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    ls.start();
                    currentLocation =  mMapView.getLocationDisplayManager().getLocation();
                    if(currentLocation!=null){
                        getWeatherData();
                    }
                }
            }
        });
        MapTouchListener tl = new MapTouchListener(this, mMapView);
        tl.setScrollview(mScrollview);
        mMapView.setOnTouchListener(tl);


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO : fix this
                Log.i("SCALE", progress + "");
                Point me = new Point(currentLocation.getLatitude(),currentLocation.getLongitude());

                mMapView.zoomToScale(me, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });



//
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //CHECKS IF GPS IS ENABLED
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
           Log.i("GPS","GPS NOT ENABLED");
           showGPSBlocker();
        }
        else{
            //START THE GPS
            Log.i("GPS","GPS ENABLED");
            //startGPS();
            //hideGPSBlocker();
        }
//        Coordinate from= new Coordinate( 18304.68,36152.73);
//        Coordinate destination= new Coordinate(21591.48,33095.24);
//        getDirectionData(from,destination);
    }

    //===================================//
    //         UI LOGIC METHODS          //
    //===================================//

    public void showGPSBlocker(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Turn on Location Service to allow app to determine your location");
        builder.setMessage("Recommendations will be presented base on your location");
        builder.setCancelable(true);
        builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        builder.setCancelable(false);
        this.GPSBlocker = builder.create();
        this.GPSBlocker.show();
    }
    public void hideGPSBlocker(){
        if(this.GPSBlocker!=null)
            this.GPSBlocker.dismiss();
    }
    //STARTS THE GPS
    public void startGPS(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);

        android.location.Location oldLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (oldLocation != null)  {
            Log.i("GPS LOCATION", "Got Old location");

            String latitude = Double.toString(oldLocation.getLatitude());
            String longitude = Double.toString(oldLocation.getLongitude());

            currentLocation = new android.location.Location(LocationManager.GPS_PROVIDER);
            currentLocation.setLatitude(oldLocation.getLatitude());
            currentLocation.setLongitude(oldLocation.getLongitude());

            Log.i("GPS LOCATION", "LAT LONG"+latitude+","+longitude);
            getWeatherData();
        } else {
            Log.i("GPS LOCATION", "NO Last Location found");
            //show the loading dialog getting current position thing
            progress = ProgressDialog.show(this, "Finding your current location",
                    "Please wait...", true);
            //progress.show();
        }
        Log.i("GPS","GPS STARTED");
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == 0){
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null&& provider.length()>0){
                //user did switch on the GPS
                Log.i("GPS SETTING RESULT", " Location providers: "+provider);
                hideGPSBlocker();
                startGPS();
            }else{
                //Users did not switch on the GPS
                showGPSBlocker();
                Log.i("GPS SETTING RESULT", " GPS NOT ON");
            }
        }
    }

    //===================================//
    //    API IMPLEMENTATION METHODS     //
    //===================================//

    //THIS IS THE METHOD TO CALL THE WEATHER DATA AND GET THE REVELENT WEATHER IN CURRENT LOCAIION
    public void getWeatherData(){
       new WeatherAPI().getWeatherData(new Callback() {
            @Override
            public void success(Object weathers, JSONObject response) {
                ArrayList<Weather> weather= ((ArrayList) weathers);

                //CACULATE THE NEAREST WEATHER RESULT USING ONEMAP API
                GeometryEngine engine = new GeometryEngine();

                Point p = new Point(weather.get(0).getAreaCoordinate().getLat(),weather.get(0).getAreaCoordinate().getLon());
                Point me = new Point(currentLocation.getLatitude(),currentLocation.getLongitude());

                double cloestDistance=engine.distance(p,me,mMapView.getSpatialReference());
                Weather cloestWeather=weather.get(0);

                for(int i=0;i<weather.size();i++){
                    p = new Point(weather.get(i).getAreaCoordinate().getLat(),weather.get(i).getAreaCoordinate().getLon());
                    if(cloestDistance>engine.distance(p,me,mMapView.getSpatialReference())) {
                        cloestDistance = engine.distance(p, me, mMapView.getSpatialReference());
                        cloestWeather = weather.get(i);
                    }
                }

                //TODO:SET THE WEATHER INFORMATION ON UI
                Log.i("CLOEST DISTANCE = ",cloestWeather.getAreaName() );
                Log.i("WEATHER = ",cloestWeather.getAreaForecast().toString());

                mWeatherCondition.setText(cloestWeather.getAreaForecast().toString().toUpperCase());

                switch(cloestWeather.getAreaForecast()){
                    case Hazy:
                        getCategoriesData(false);
                        mWeatherIcon.setImageResource(R.drawable.hazy);
                        break;
                    case FairDAY:
                        getCategoriesData(true);
                        mWeatherIcon.setImageResource(R.drawable.clear);
                        break;
                    case FairNIGHT:
                        getCategoriesData(true);
                        mWeatherIcon.setImageResource(R.drawable.nt_clear);
                        break;
                    case Cloudy:
                        getCategoriesData(true);
                        mWeatherIcon.setImageResource(R.drawable.cloudy);
                        break;
                    case PartlyCloudy:
                        getCategoriesData(true);
                        mWeatherIcon.setImageResource(R.drawable.partlycloudy);
                        break;
                    case Windy:
                        getCategoriesData(true);
                        mWeatherIcon.setImageResource(R.drawable.hazy);
                        break;
                    case Rain:
                        getCategoriesData(false);
                        mWeatherIcon.setImageResource(R.drawable.rain);
                        break;
                    case PassingShowers:
                        getCategoriesData(false);
                        mWeatherIcon.setImageResource(R.drawable.chancerain);
                        break;
                    case Showers:
                        getCategoriesData(false);
                        mWeatherIcon.setImageResource(R.drawable.chancetstorms);
                        break;
                    case Thunderyshowers:
                        getCategoriesData(false);
                        mWeatherIcon.setImageResource(R.drawable.tstorms);
                        break;
                }

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
       new LocationsAPI().getLocationsFromCategories(categories, new Callback() {
           @Override
           public void success(Object locations, JSONObject response) {
               ArrayList<Location> location = ((ArrayList) locations);
               //for some reason it starts from index 1
               x++;
               Log.i("categories count", x + "");
               if (x == categories.length) {
                   //start picking the random one base on distance from gps
                   Log.i("SUCCESS location API CALL", location.get(1).getName().toString());
                   Log.i("SUCCESS location hawker API CALL", location.get(location.size() - 1).getName().toString());
               }
           }

           @Override
           public void failure(String error) {

           }
       });
    }

    //THIS IS THE METHOD TO GT THE CATEGORY BASE ON THE WEATHER, TRUE=GOOD, FALSE=BAD
    public void getCategoriesData(boolean is_good_weather){
        ArrayList<Location.Category> categories = new CategoryAPI().getCategoriesOptions(is_good_weather);
       //set the category adapter and init the list
    }


    //===================================//
    //GPS LISTENER IMPLEMENTATION METHODS//
    //===================================//
    @Override
    public void onLocationChanged(android.location.Location location) {
        currentLocation = location;
        Log.i("CURRENT LOCATION",location.toString());
        progress.dismiss();
        //ONCE WE GET THE LOCATION WE STOP GPS AND CALL GET WEATHER API
        locationManager.removeUpdates(this);
        getWeatherData();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //cant remember the status meaning need to check it out on ur own
        switch (status) {
            case 0:
                // Do Something with mStatus info
                Log.i("GPS STATUS CHANGE","GPS OUT OF SERVICE");
                break;
            case 1:
                // Do Something with mStatus info
                Log.i("GPS STATUS CHANGE","GPS TEMP OUT OF SERVICE");
                break;
            case 2:
                // Do Something with mStatus info
                Log.i("GPS STATUS CHANGE","GPS AVAILABLE");
                break;
        }
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




    //==========================================//
    //SCROLLVIEW LISTENER IMPLEMENTATION METHODS//
    //==========================================//

//    @Override
//    protected ObservableRecyclerView createScrollable() {
//        ObservableRecyclerView recyclerView = (ObservableRecyclerView) findViewById(R.id.scroll);
//        recyclerView.setScrollViewCallbacks(this);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(false);
////        setDummyDataWithHeader(recyclerView, mFlexibleSpaceImageHeight);
//        return recyclerView;
//    }
//
//    @Override
//    protected int getLayoutResId() {
//        return R.layout.activity_fillgaprecyclerview;
//    }
//
////    @Override
////    protected void updateViews(int scrollY, boolean animated) {
//////        super.updateViews(scrollY, animated);
////
////        // Translate list background
//////        ViewHelper.setTranslationY(mListBackgroundView, ViewHelper.getTranslationY(mHeader));
////    }

//    @Override
//    public void onScrollChanged(int i, boolean b, boolean b2) {
//        Log.i("i",""+i);
//    }
//
//    @Override
//    public void onDownMotionEvent() {
//
//    }
//
//    @Override
//    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
//        Log.i("",""+scrollState);
//    }
}
