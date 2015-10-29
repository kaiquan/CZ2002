package sg.ntu.cz2002.controller;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import sg.ntu.cz2002.entity.User;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class APIController {

    public static final String KEY_SETTINGS_URL_WEATHER_API = "http://www.nea.gov.sg/api/WebAPI/?dataset=nowcast&keyref=781CF461BB6606AD19AA45F38E88F174DE3B00D432472A50";
    public static final String KEY_SETTINGS_URL_PLACES_API = "http://www.onemap.sg/API/services.svc/mashupData?";
    public static final String KEY_SETTINGS_URL_DIRECTION_API="http://www.onemap.sg/API/services.svc/route/solve?";
    public static final String KEY_SETTINGS_ONEMAP_TOKEN = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
    public static final String KEY_SETTINGS_ONEMAP_THEME_MUSEUM="MUSEUM";
    public static final String KEY_SETTINGS_ONEMAP_THEME_HAWKERCENTER="HAWKERCENTRE";
    public static final String KEY_SETTINGS_ONEMAP_THEME_LIBRARIES="LIBRARIES";
    public static final String KEY_SETTINGS_ONEMAP_THEME_WATERVENTURE="WATERVENTURE";
    public static final String KEY_SETTINGS_ONEMAP_THEME_PARK="NATIONALPARKS";
    public static final String KEY_SETTINGS_ONEMAP_THEME_TOURISM="TOURISM";
    public static Context context;
    private static APIController mInstance;
    private static AsyncHttpClient client = new AsyncHttpClient();
//    private final WeatherController weatherAPI;
//    private final LocationsController locationAPI;
//    private final DirectionsController directionAPI;
//    private final CategoriesController categoryAPI;
//    private final User userAPI;

    public APIController() {
//        this.weatherAPI = new WeatherController();
//        this.locationAPI = new LocationsController();
//        this.directionAPI = new DirectionsController();
//        this.categoryAPI = new CategoriesController();
//        this.userAPI = new User();
    }

//    public static APIController getInstance() {
//        if (mInstance == null) {
//            mInstance = new APIController();
//        }
//        return mInstance;
//    }

    public static RequestHandle GET(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return GET(url, null, params, responseHandler);
    }

    public static RequestHandle GET(String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return client.get(context, url, headers, params, responseHandler);
    }


//    public LocationsController getLocationAPI(){return  locationAPI;}
//    public CategoriesController getCategoryAPI(){return  categoryAPI;}
//    public WeatherController getWeatherAPI() {
//        return weatherAPI;
//    }
//    public DirectionsController getDirectionAPI() {
//        return directionAPI;
//    }


}

