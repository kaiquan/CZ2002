package sg.ntu.cz2002.controller;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Location;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */
public class LocationsAPI extends APIController{

    private static ArrayList<Location> locations=null;
    private int i=0;

    public int getI() {
        return i;
    }


    /**
     * @author      : kai quan
     * @param       : ArrayList<Category>, Callback
     * @return      : void
     * @description : API call for getting location information
     * */
    public void getLocationsFromCategories(final ArrayList<Location.Category> categories, final Callback callback){

        RequestParams params;

        for(i=0;i<categories.size();i++){

            params = new RequestParams();
            params.add("token", APIController.KEY_SETTINGS_ONEMAP_TOKEN);
            params.add("otptFlds","HYPERLINK,NAME");

            if(categories.get(i).equals(Location.Category.HawkerCentres)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_HAWKERCENTER);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.HawkerCentres);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
            else if(categories.get(i).equals(Location.Category.Libraries)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_LIBRARIES);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.Libraries);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
            else if(categories.get(i).equals(Location.Category.Museums)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_MUSEUM);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.Museums);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
            else if(categories.get(i).equals(Location.Category.Parks)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_PARK);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.Parks);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
            else if(categories.get(i).equals(Location.Category.TouristAttractions)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_TOURISM);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.TouristAttractions);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
            else if(categories.get(i).equals(Location.Category.WaterVentures)){
                params.add("themeName", APIController.KEY_SETTINGS_ONEMAP_THEME_WATERVENTURE);
                Log.i("THEME",categories.get(i).toString());
                GET(APIController.KEY_SETTINGS_URL_PLACES_API,params,new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        storeData(callback, response, Location.Category.WaterVentures);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                    }
                });
            }
        }
    }

    /**
     * @author      : kai quan
     * @param       : Callback, JSONObject, Category
     * @return      : void
     * @description : converting JSONObject to Location objects
     * */
    public void storeData(Callback callback, JSONObject object, Location.Category categoryType){
        Location location;
        if(locations==null)
            locations= new ArrayList<>();
        JSONArray results=null;
        try {
            results = object.getJSONArray("SrchResults");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(results!=null){
            for(int x=1;x<results.length()-1;x++){
                location = new Location();
                JSONObject temp;
                try {
                    temp =results.getJSONObject(x);
                    Log.i("LOCAITONJSON-",temp.toString());
//                    location.setHyperlink(temp.getString("HYPERLINK"));
                    //location.setDescriptions(temp.getString("DESCRIPTION"));
                    location.setName(temp.getString("NAME"));
                    //location.setAddress(temp.getString("ADDRESSSTREETNAME"));
                    location.setIconURL(temp.getString("ICON_NAME"));
                    location.setCategory(categoryType);

                    String xy=temp.getString("XY");
                    String[] data = xy.split(",");
                    location.setCoordinate(new Coordinate(Double.valueOf(data[0]),Double.valueOf(data[1])));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                locations.add(location);
            }
            callback.success(locations,null);
        }
    }
}
