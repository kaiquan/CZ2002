package sg.ntu.cz2002.controller;
import android.util.Log;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Weather;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */
public class WeatherController extends APIController{

    /**
     * @author      : Lee kai quan
     * @param       : Callback
     * @return      : void
     * @description : API call for getting weather information
     * */
    public void getWeatherData(final Callback callback){
        GET(APIController.KEY_SETTINGS_URL_WEATHER_API,null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                if (null == response)
                    callback.failure("Data set requested was not available");
                String xmlString = getResponseString(response, getCharset());
                if(xmlString == null)
                    callback.failure("Data set requested was not available");
                else{
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = XML.toJSONObject(xmlString);
                    } catch (JSONException e) {
                        Log.e("JSON exception at WEATHER API", e.getMessage());
                        e.printStackTrace();
                    }
                    if(jsonObj == null)
                        callback.failure("Data set requested was not available");
                    else{
                        ArrayList<Weather> weathers = null;
                        try {
                            weathers = getObjectsFromJSON(jsonObj);
                        } catch (JSONException e) {
                            Log.e("JSON exception at WEATHER API", e.getMessage());
                            callback.failure("Data set requested was not available");
                        }
                        callback.success(weathers,jsonObj);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                if(statusCode==401)
                    callback.failure("nea-authorization-key used was not valid");
                else if(statusCode==404)
                    callback.failure("Data set requested was not available");

            }
        });
    }

    /**
     * @author      : kai quan
     * @param       : byte[] , String
     * @return      : String
     * @description : convert xml data to json string
     * */
    private String getResponseString(byte[] stringBytes, String charset) {
        try {
            return stringBytes == null ? null : new String(stringBytes, charset);
        } catch (UnsupportedEncodingException e) {
            Log.e("WEATHER API ERROR", "Encoding response into string failed", e);
            return null;
        }
    }

    /**
     * @author      : kai quan
     * @param       : JSONObject
     * @return      : ArrayList<Weather>
     * @description : convert JSON data to Weather objects
     * */
    private ArrayList<Weather> getObjectsFromJSON(JSONObject jsonObject) throws JSONException {
        JSONObject channel= jsonObject.getJSONObject("channel");
        ArrayList<Weather> Weathers= new ArrayList<>();
        Weather weather;
        for(int i=0;i<channel.getJSONObject("item").getJSONObject("weatherForecast").getJSONArray("area").length();i++){
            JSONObject area = channel.getJSONObject("item").getJSONObject("weatherForecast").getJSONArray("area").getJSONObject(i);
            weather = new Weather(area);

            Weathers.add(weather);
        }
        return Weathers;
    }
}
