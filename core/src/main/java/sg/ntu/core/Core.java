package sg.ntu.core;

import android.content.Context;

import com.dd.plist.NSDictionary;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

import sg.ntu.core.api.CategoryAPI;
import sg.ntu.core.api.DirectionAPI;
import sg.ntu.core.api.LocationsAPI;
import sg.ntu.core.api.WeatherAPI;
import sg.ntu.core.models.User;

public class Core {

    public static final String KEY_SETTINGS_URL_WEATHER_API = "http://www.nea.gov.sg/api/WebAPI/?dataset=nowcast&keyref=781CF461BB6606AD19AA45F38E88F174DE3B00D432472A50";
    public static final String KEY_SETTINGS_URL_PLACES_API = "http://www.onemap.sg/API/services.svc/mashupData?";
    public static final String KEY_SETTINGS_ONEMAP_TOKEN = "qo/s2TnSUmfLz+32CvLC4RMVkzEFYjxqyti1KhByvEacEdMWBpCuSSQ+IFRT84QjGPBCuz/cBom8PfSm3GjEsGc8PkdEEOEr";
    public static final String KEY_SETTINGS_ONEMAP_THEME_MUSEUM="MUSEUM";
    public static final String KEY_SETTINGS_ONEMAP_THEME_HAWKERCENTER="HAWKERCENTRE";
    public static final String KEY_SETTINGS_ONEMAP_THEME_LIBRARIES="LIBRARIES";
    public static final String KEY_SETTINGS_ONEMAP_THEME_WATERVENTURE="WATERVENTURE";
    public static final String KEY_SETTINGS_ONEMAP_THEME_PARK="NATIONALPARKS";
    public static final String KEY_SETTINGS_ONEMAP_THEME_TOURISM="TOURISM";
    public static Context context;
    private static Core mInstance;
    private static AsyncHttpClient client = new AsyncHttpClient();
    private final WeatherAPI weatherAPI;
    private final LocationsAPI locationAPI;
    private final DirectionAPI directionAPI;
    private final CategoryAPI categoryAPI;
    private final User userAPI;
    public NSDictionary settings;

    private Core() {
        this.weatherAPI = new WeatherAPI();
        this.locationAPI = new LocationsAPI();
        this.directionAPI = new DirectionAPI();
        this.categoryAPI = new CategoryAPI();
        this.userAPI = new User();
    }

    public static Core getInstance() {
        if (mInstance == null) {
            mInstance = new Core();
        }
        return mInstance;
    }

    public static RequestHandle GET(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return GET(url, null, params, responseHandler);
    }

    public static RequestHandle GET(String url, Header[] headers, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        return client.get(context, url, headers, params, responseHandler);
    }


    public LocationsAPI getLocationAPI(){return  locationAPI;}
    public CategoryAPI getCategoryAPI(){return  categoryAPI;}
    public WeatherAPI getWeatherAPI() {
        return weatherAPI;
    }
    public DirectionAPI getDirectionAPI() {
        return directionAPI;
    }




    public interface Callback<T> {

        /**
         * Successful HTTP response.
         */
        void success(T t, JSONObject response);

        /**
         * Unsuccessful HTTP response due to network failure, non-2XX status code, or unexpected
         * exception.
         */
        void failure(String error);
    }
}

