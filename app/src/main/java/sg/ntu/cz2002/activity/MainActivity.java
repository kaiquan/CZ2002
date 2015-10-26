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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.CalloutPopupWindow;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import sg.ntu.cz2002.adapter.CategoryAdapter;
import sg.ntu.cz2002.controller.APIController;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.controller.Callback;
import sg.ntu.cz2002.controller.CategoryAPI;
import sg.ntu.cz2002.controller.LocationsAPI;
import sg.ntu.cz2002.controller.WeatherAPI;
import sg.ntu.cz2002.entity.Location;
import sg.ntu.cz2002.entity.Weather;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class MainActivity extends Activity implements LocationListener {

    private Context mContext;
    private int x=0;
    private LocationManager locationManager;
    private android.location.Location currentLocation;
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

    Graphic graphic;



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
                mSeekerRange.setText(progress+"KM");
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


        //init the gps listener
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
    private void startGPS(){
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
//            progress = ProgressDialog.show(this, "Finding your current location",
//                    "Please wait...", true);
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
                //TODO test this methos
                while(currentLocation==null){
                    //wait
                }
                getWeatherData();
//                startGPS();
            }else{
                //Users did not switch on the GPS
                showGPSBlocker();
                Log.i("GPS SETTING RESULT", " GPS NOT ON");
            }
        }
    }
    //TODO LAST DIRECTION METHOD
    private void navigateToDirectionActivity(Location to, android.location.Location currentLocation){
        Intent intent = new Intent(this, DirectionActivity.class);
        intent.putExtra("DIRECTION_FROM", currentLocation.getLatitude()+","+currentLocation.getLongitude());
        intent.putExtra("DIRECTION_TO", to.getCoordinate().getLat()+","+to.getCoordinate().getLon());
        intent.putExtra("LOCATION_NAME",to.getName());
        startActivity(intent);
    }
    private double caculateLength(Point to, Point from){
        Double R = 6371.0;
        Double dLat = toRadian(to.getY() - from.getY());
        Double dLong = toRadian(to.getX() - from.getX());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(toRadian(to.getY())) * Math.cos(toRadian(from.getY())) *
                        Math.sin(dLong / 2) * Math.sin(dLong / 2);
        double c = 2 * Math.asin(Math.min(1, Math.sqrt(a)));

        return (R * c);
    }
    private static double toRadian(double val)
    {
        return (Math.PI / 180) * val;
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
    private void generateAndDisplayRandomLocation(){
        for(int i=0;i<locationsToSelect.size();i++){
            Point to = new Point(locationsToSelect.get(i).getCoordinate().getLat(),locationsToSelect.get(i).getCoordinate().getLon());
            Point from = new Point(currentLocation.getLatitude(),currentLocation.getLongitude());
            double distance = caculateLength(to, from);
            Log.i("DISTANCE is ",distance+"");

            //TODO FIX THIS PROBLEM OR START FROM 1KM
            if(distance>(mSeekBar.getProgress()*1000)){
                Log.i("RANGE is =",mSeekBar.getProgress()*1000+"");
                Log.i("REMOVING ",locationsToSelect.get(i).getName()+"="+distance);
                locationsToSelect.remove(i);
            }
            else{
                Log.i("KEEPING ",locationsToSelect.get(i).getName()+"="+distance);
            }
        }
        Random rand = new Random();
        if(progress!=null)
            progress.dismiss();
        if(locationsToSelect==null||locationsToSelect.size()==0){
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
            int randomNum = rand.nextInt((locationsToSelect.size()-1 - 0) + 1) + 0;

            Log.i("RANDOM LOCITON +",locationsToSelect.get(randomNum).getName());
            Log.i("RANDOM LOCITON xy+",locationsToSelect.get(randomNum).getCoordinate().getLat()+","+locationsToSelect.get(randomNum).getCoordinate().getLon());
            plotPoint(locationsToSelect.get(randomNum));
        }
    }
    private void plotPoint(Location location){
        GraphicsLayer graphicsLayer = new GraphicsLayer();
        mMapView.addLayer(graphicsLayer);
        Point point = new Point(location.getCoordinate().getLat(), location.getCoordinate().getLon());
        graphicsLayer.addGraphic(new Graphic(point,new PictureMarkerSymbol(this,getDrawable(R.drawable.pin)).setOffsetY(50)));
        mMapView.addLayer(graphicsLayer);

        mMapView.zoomTo(point,20.0f);

//
        //TODO  CLICKABLE CALLOUT TO NAVIGATE TO DIRECTION ACTIVITy
//        CalloutPopupWindow callout = new CalloutPopupWindow(, CalloutPopupWindow.MODE.CLIP, null);
//        callout.showCallout(mMapView, point, 0, 0);


//        mMapView.setOnSingleTapListener(new OnSingleTapListener() {
//
//            @Override
//            public void onSingleTap(float x, float y) {
//                // mapPoint = mMapView.toMapPoint(x, y);
//                identifyLocation(x, y);
//
//                ShowCallout(mMapView.getCallout(),graphic, new Point(x,y));
//            }
//        });
    }
    private void identifyLocation(float x, float y) {

        // Hide the callout, if the callout from previous tap is still showing
//        if (m_callout.isShowing()) {
//            m_callout.hide();
//        }

        Point mapPoint = mMapView.toMapPoint(x, y);
        SimpleMarkerSymbol sms = new SimpleMarkerSymbol(Color.GREEN, 25,
        SimpleMarkerSymbol.STYLE.CROSS);

        Map<String, Object> hm = new HashMap<String, Object>();
        hm.put("NAME", "Amman");
        hm.put("COUNTRY", "Jordan");
        graphic = new Graphic(mapPoint, sms, hm);
        GraphicsLayer locationLayer = new GraphicsLayer();

        locationLayer.addGraphic(graphic);
        mMapView.addLayer(locationLayer);
    }
    private void ShowCallout(Callout calloutView, Graphic graphic,
                             Point mapPoint) {
        Log.v("call", "in the callout");
        // Get the values of attributes for the Graphic
//        String cityName = (String) graphic.getAttributeValue("NAME");
//        String countryName = (String) graphic.getAttributeValue("COUNTRY");
        // String cityPopulationValue = ((Double) graphic
        // .getAttributeValue("POPULATION")).toString();
        Log.v("call", "so far so good");
        // Set callout properties
        calloutView.setCoordinates(mapPoint);
//        calloutView.setStyle(m_calloutStyle);
        calloutView.setMaxWidth(325);

        // Compose the string to display the results
        StringBuilder cityCountryName = new StringBuilder();
//        cityCountryName.append(cityName);
//        cityCountryName.append(", ");
//        cityCountryName.append(countryName);

//        TextView calloutTextLine1 = (TextView) findViewById(R.id.citycountry);
//        calloutTextLine1.setText(cityCountryName);

        // Compose the string to display the results
        StringBuilder cityPopulation = new StringBuilder();
        cityPopulation.append("Population: ");
        // cityPopulation.append(cityPopulationValue);

        // TextView calloutTextLine2 = (TextView) findViewById(R.id.population);
        // calloutTextLine2.setText(cityPopulation);
//        calloutView.setContent(calloutContent);
        calloutView.show();
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
                displayWeatherInformation(weather);
            }
            @Override
            public void failure(String error) {
                Log.e("FAILURE WEATHER API CALL",error);
            }
        });
    }

    //THIS IS THE METHOD TO GET THE LOCATIONS FROM THE SELECTED CATEGORY
    public void getLocationsData(){
       x=0;
       progress = ProgressDialog.show(this, "Finding a recommended place for you","Please hold on...", true);
       new LocationsAPI().getLocationsFromCategories(categories, new Callback() {
           @Override
           public void success(Object locations,JSONObject response) {
               ArrayList<Location> location = ((ArrayList) locations);
               if(locationsToSelect==null)
                   locationsToSelect = new ArrayList<>();
               locationsToSelect.addAll(location);
               x++;
               Log.i("categories count", location.size() + "");
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
    public void getCategoriesData(boolean is_good_weather){
        ArrayList<Location.Category> categories = new CategoryAPI().getCategoriesOptions(is_good_weather);
        categoryAdapter = new CategoryAdapter(this, R.layout.categroylist,categories);
        mCategoryList.setAdapter(categoryAdapter);

        if(is_good_weather)
            mCategoryList.setLayoutParams(new LinearLayout.LayoutParams(mScrollview.getLayoutParams().width, 720));
        else
            mCategoryList.setLayoutParams(new LinearLayout.LayoutParams(mScrollview.getLayoutParams().width, 600));
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
}
