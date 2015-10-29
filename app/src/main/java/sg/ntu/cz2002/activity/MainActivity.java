package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.TextSymbol;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import sg.ntu.cz2002.adapter.CategoryAdapter;
import sg.ntu.cz2002.controller.APIController;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.controller.Callback;
import sg.ntu.cz2002.controller.CategoriesController;
import sg.ntu.cz2002.controller.LocationsController;
import sg.ntu.cz2002.controller.SVY21;
import sg.ntu.cz2002.controller.WeatherController;
import sg.ntu.cz2002.entity.LatLonCoordinate;
import sg.ntu.cz2002.entity.Location;
import sg.ntu.cz2002.entity.SVY21Coordinate;
import sg.ntu.cz2002.entity.Weather;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class MainActivity extends Activity{

    private Context mContext;
    private int x=0;
    private LocationManager locationManager;
    private android.location.Location currentLocation;
    private android.location.Location currentLocationSy;
    private AlertDialog GPSBlocker = null;
    private int REQUEST_CODE;
    private CategoryAdapter categoryAdapter;
    private MapView mMapView = null;
    private ArrayList<Location.Category> categories;
    private ArrayList<Location> locationsToSelect;

    private Button mGoBtn;

    private ScrollView mScrollview;
    private ProgressDialog progress;
    private SeekBar mSeekBar;

    private ImageView mWeatherIcon;
    private TextView mWeatherCondition,mSeekerRange;
    private ListView mCategoryList;
    private Location selectedLocation;

    Graphic graphic;
    LocationDisplayManager ls;
    GraphicsLayer graphicsLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        APIController.context=getApplicationContext();
        init();
    }
    //THIS IS THE INITIAL METHODS FOR UR R.id MAPPING AND STARTING GPS
    public void init(){
        mContext=this;
        mScrollview = (ScrollView)findViewById(R.id.scrollview);
        mSeekBar = (SeekBar)findViewById(R.id.seekbar);
        mGoBtn = (Button)findViewById(R.id.goBtn);
        mWeatherCondition = (TextView)findViewById(R.id.weatherconditionTxt);
        mWeatherIcon = (ImageView)findViewById(R.id.weathericon);
        mCategoryList = (ListView)findViewById(R.id.categoryList);
        mSeekerRange = (TextView)findViewById(R.id.seekerRange);


        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSeekerRange.setText((progress+1)+"KM");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(categoryAdapter.getSelectedCategories()==null||categoryAdapter.getSelectedCategories().size()==0){
                   new AlertDialog.Builder(mContext)
                            .setTitle("Alert!")
                            .setMessage("Select a category to continue")
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                  dialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    categories = categoryAdapter.getSelectedCategories();
                    locationsToSelect = new ArrayList<>();
                    getLocationsData();
                }
            }
        });

        //init the map view
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.enableWrapAround(true);
        ArcGISRuntime.setClientId("j9r0J2JIy8FFFfB8");
        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://www.onemap.sg/ArcGIS/rest/services/BASEMAP/MapServer"));
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            public void onStatusChanged(Object source, STATUS status) {
                if (source == mMapView && status == STATUS.INITIALIZED) {
                    ls = mMapView.getLocationDisplayManager();
                    ls.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
                    ls.setLocationListener(new LocationListener() {
                        @Override
                        public void onLocationChanged(android.location.Location location) {
                            currentLocation = location;
                            Log.i("CURRENT LOCATION i",location.toString());
                            if(progress!=null)
                                progress.dismiss();
                            //ONCE WE GET THE LOCATION WE STOP GPS AND CALL GET WEATHER API
                            locationManager.removeUpdates(this);
                            getWeatherData();
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                        }
                    });
                    ls.start();
                    currentLocation = mMapView.getLocationDisplayManager().getLocation();
                    if(currentLocation!=null){
                        getWeatherData();
                    }
                }
            }
        });
        MapTouchListener tl = new MapTouchListener(this, mMapView);
        tl.setScrollview(mScrollview);
        mMapView.setOnTouchListener(tl);

        //init the gps listener
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //CHECKS IF GPS IS ENABLED
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled){
           showGPSBlocker();
        }
        else{
            //START THE GPS
            hideGPSBlocker();
        }
    }

    //===================================//
    //         UI LOGIC METHODS          //
    //===================================//

        private void showGPSBlocker(){
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
    private void hideGPSBlocker(){
        if(this.GPSBlocker!=null)
            this.GPSBlocker.dismiss();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE && resultCode == 0){
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if(provider != null&& provider.length()>0){
                //user did switch on the GPS
                Log.i("GPS SETTING RESULT", " Location providers: " + provider);
                hideGPSBlocker();
                progress = ProgressDialog.show(this, "Finding your current location", "Please wait...", true);
                progress.show();
                ls.setLocationListener(new LocationListener() {
                    @Override
                    public void onLocationChanged(android.location.Location location) {
                        currentLocation = location;
                        Log.i("CURRENT LOCATION i", location.toString());
                        if (progress != null)
                            progress.dismiss();
                        //ONCE WE GET THE LOCATION WE STOP GPS AND CALL GET WEATHER API
                        locationManager.removeUpdates(this);
                        getWeatherData();
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                });
                ls.start();
            }else{
                //Users did not switch on the GPS
                showGPSBlocker();
                Log.i("GPS SETTING RESULT", " GPS NOT ON");
            }
        }
    }
    private void navigateToDirectionActivity(Location to, android.location.Location currentLocation){
        Intent intent = new Intent(this, DirectionActivity.class);
        Log.i("INTEND",currentLocation.getLatitude()+"");
        intent.putExtra("DIRECTION_FROM_LAT", currentLocation.getLatitude()+"");
        intent.putExtra("DIRECTION_FROM_LON", currentLocation.getLongitude()+"");
        intent.putExtra("DIRECTION_TO_LAT", to.getCoordinate().getLat()+"");
        intent.putExtra("DIRECTION_TO_LON", to.getCoordinate().getLon()+"");
        intent.putExtra("LOCATION_NAME",to.getName());
        intent.putExtra("LOCATION_ADDRESS",to.getAddress());
        startActivity(intent);
    }
    private void displayWeatherInformation(ArrayList<Weather> weathers){
        GeometryEngine engine = new GeometryEngine();

        Point p = new Point(weathers.get(0).getAreaCoordinate().getLat(),weathers.get(0).getAreaCoordinate().getLon());
        Point me = new Point(currentLocation.getLatitude(),currentLocation.getLongitude());

        double cloestDistance= GeometryEngine.distance(p, me, mMapView.getSpatialReference());
        Weather myWeather=weathers.get(0);

        for(int i=0;i<weathers.size();i++){
            p = new Point(weathers.get(i).getAreaCoordinate().getLat(),weathers.get(i).getAreaCoordinate().getLon());
            if(cloestDistance>engine.distance(p,me,mMapView.getSpatialReference())) {
                cloestDistance = engine.distance(p, me, mMapView.getSpatialReference());
                myWeather = weathers.get(i);
            }
        }

        Log.i("CLOEST DISTANCE = ",myWeather.getAreaName() );
        Log.i("WEATHER = ",myWeather.getAreaForecast().toString());

        mWeatherCondition.setText(myWeather.getAreaForecast().toString().toUpperCase());

        switch(myWeather.getAreaForecast()){
            case Hazy:
                showCategories(false);
                mWeatherIcon.setImageResource(R.drawable.hazy);
                break;
            case FairDAY:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.clear);
                break;
            case Fair:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.nt_clear);
                break;
            case FairNIGHT:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.nt_clear);
                break;
            case Cloudy:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.cloudy);
                break;
            case Partlycloudy:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.partlycloudy);
                break;
            case Windy:
                showCategories(true);
                mWeatherIcon.setImageResource(R.drawable.hazy);
                break;
            case Rain:
                showCategories(false);
                mWeatherIcon.setImageResource(R.drawable.rain);
                break;
            case PassingShowers:
                showCategories(false);
                mWeatherIcon.setImageResource(R.drawable.chancerain);
                break;
            case Showers:
                showCategories(false);
                mWeatherIcon.setImageResource(R.drawable.chancetstorms);
                break;
            case Thunderyshowers:
                showCategories(false);
                mWeatherIcon.setImageResource(R.drawable.tstorms);
                break;
        }
        convertCoordinateFormat();
    }
    private void generateAndDisplayRandomLocation(){

        ArrayList<Location> locations = new ArrayList<>();
        for(int i=0;i<locationsToSelect.size()-1;i++){
            Point to = new Point(locationsToSelect.get(i).getCoordinate().getLat(),locationsToSelect.get(i).getCoordinate().getLon());
            Point from = new Point(currentLocationSy.getLatitude(),currentLocationSy.getLongitude());
            double distance = new GeometryEngine().distance(to, from, SpatialReference.create(3414));
            if(distance<=((mSeekBar.getProgress()+1)*1000)){
                locations.add(locationsToSelect.get(i));
                Log.i("RESULTS TO CHOOSE ",locationsToSelect.get(i).getName()+" "+distance+"");
            }else{
//                Log.i("RESULTS TO CHOOSE ",locationsToSelect.get(i).getName()+" "+distance+"");
            }
        }

        for(int i=0;i<locations.size()-1;i++){
            Point to = new Point(locations.get(i).getCoordinate().getLat(),locations.get(i).getCoordinate().getLon());
            Point from = new Point(currentLocationSy.getLatitude(),currentLocationSy.getLongitude());
            double distance = new GeometryEngine().distance(to, from, SpatialReference.create(3414));
            Log.i("RESULTS TO CHOOSE ",locations.get(i).getName()+" "+distance+"");
        }
        Random rand = new Random();
        if(progress!=null)
            progress.dismiss();
        if(locations==null||locations.size()==0){
            new AlertDialog.Builder(mContext)
                    .setTitle("Alert!")
                    .setMessage("Oops.. no place around here. Try increasing the range =)")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
        else{
            int randomNum = rand.nextInt((locations.size()-1 - 0) + 1) + 0;

//            Log.i("RANDOM LOCITON +",locationsToSelect.get(randomNum).getName());
//            Log.i("RANDOM LOCITON xy+",locationsToSelect.get(randomNum).getCoordinate().getLat()+","+locationsToSelect.get(randomNum).getCoordinate().getLon());
            selectedLocation = locations.get(randomNum);
            plotPoint(selectedLocation);

        }
    }
    private void plotPoint(Location location){
        Point to = new Point(location.getCoordinate().getLat(),location.getCoordinate().getLon());
        Point from = new Point(currentLocationSy.getLatitude(),currentLocationSy.getLongitude());
        double distance = new GeometryEngine().distance(to, from, SpatialReference.create(3414));
        Log.i("SELECTED LOCATION",location.getName()+" D="+distance);
        if(graphicsLayer!=null)
            mMapView.removeLayer(graphicsLayer);

        graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);
        Point point = new Point(location.getCoordinate().getLat(), location.getCoordinate().getLon());
        graphicsLayer.addGraphic(new Graphic(point,new PictureMarkerSymbol(this,getDrawable(R.drawable.pin))));
        mMapView.addLayer(graphicsLayer);

        mMapView.zoomTo(point,20.0f);
        TextSymbol bassRockSymbol =
                new TextSymbol(
                        10, "Bass Rock", Color.BLUE,
                        TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM);
        Graphic bassRockGraphic = new Graphic(point, bassRockSymbol.setOffsetX(50));
        graphicsLayer.addGraphic(bassRockGraphic);
        mMapView.addLayer(graphicsLayer);

        ViewGroup calloutContent = (ViewGroup) getLayoutInflater().inflate(
                R.layout.callout_layout, null);
        ((TextView)calloutContent.findViewById(R.id.name)).setText(selectedLocation.getName());
        (calloutContent.findViewById(R.id.callout_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToDirectionActivity(selectedLocation,currentLocationSy);
            }
        });


        mMapView.getCallout().setContent(calloutContent);
        mMapView.getCallout().setCoordinates(point);
        mMapView.getCallout().show();

    }

    //===================================//
    //    API IMPLEMENTATION METHODS     //
    //===================================//

    //THIS IS THE METHOD TO CALL THE WEATHER DATA AND GET THE REVELENT WEATHER IN CURRENT LOCAIION
    public void getWeatherData(){
        ls.setLocationListener(null);
        ls.stop();
       new WeatherController().getWeatherData(new Callback() {
            @Override
            public void success(Object weathers, JSONObject response) {
                ArrayList<Weather> weather= ((ArrayList) weathers);
                displayWeatherInformation(weather);
//                convertCoordinateFormat();
            }
            @Override
            public void failure(String error) {
                Log.e("FAILURE WEATHER API CALL",error);
            }
        });
    }
    private void convertCoordinateFormat(){
        Log.i("CURRENT LAT LON1",currentLocation.getLatitude()+","+currentLocation.getLongitude());

        LatLonCoordinate coord1 = new LatLonCoordinate(currentLocation.getLatitude(),currentLocation.getLongitude());

        SVY21Coordinate result = coord1.asSVY21();

        result = SVY21.computeSVY21(coord1);

        double lat = coord1.getLatitude();
        double lon = coord1.getLongitude();
        result = SVY21.computeSVY21(lat, lon);

        LatLonCoordinate reverseResult = result.asLatLon();

        reverseResult = SVY21.computeLatLon(result);

        double northing = result.getNorthing();
        double easting = result.getEasting();
        reverseResult = SVY21.computeLatLon(northing, easting);


       Log.i("CONVERTED LAT",result.getNorthing()+","+result.getEasting());
        currentLocationSy = new android.location.Location(currentLocation);
        currentLocationSy.setLatitude(result.getNorthing());
        currentLocationSy.setLongitude(result.getEasting());
    }
    //THIS IS THE METHOD TO GET THE LOCATIONS FROM THE SELECTED CATEGORY
    public void getLocationsData(){
        locationsToSelect=null;
       x=0;
       progress = ProgressDialog.show(this, "Finding a recommended place for you","Please hold on...", true);
       new LocationsController().getLocationsFromCategories(categories, new Callback() {
           @Override
           public void success(Object locations,JSONObject response) {

               ArrayList<Location> location = ((ArrayList) locations);
               if(locationsToSelect==null)
                   locationsToSelect = new ArrayList<>();
               locationsToSelect.addAll(location);
               x++;
               Log.i("categories count", categories.size() + "x is "+x+"");
               if (x == categories.size()) {
                   generateAndDisplayRandomLocation();
               }
           }

           @Override
           public void failure(String error) {
               Log.e("FAILURE LOCATION API CALL",error);
           }
       });
    }
    //THIS IS THE METHOD TO GET THE CATEGORY LIST BASE ON THE WEATHER
    public void showCategories(boolean isGoodWeather){
        ArrayList<Location.Category> categories = new CategoriesController().getCategoriesOptions(isGoodWeather);
        categoryAdapter = new CategoryAdapter(this, R.layout.categroylist,categories);
        mCategoryList.setAdapter(categoryAdapter);

        if(isGoodWeather)
            mCategoryList.setLayoutParams(new LinearLayout.LayoutParams(mScrollview.getLayoutParams().width, 720));
        else
            mCategoryList.setLayoutParams(new LinearLayout.LayoutParams(mScrollview.getLayoutParams().width, 600));
    }


}
