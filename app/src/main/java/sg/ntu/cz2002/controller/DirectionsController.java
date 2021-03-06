package sg.ntu.cz2002.controller;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Direction;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */
//TODO write doc block
public class DirectionsController extends APIController {
    private Direction direction;


    public void getDirection(Coordinate from,Coordinate destination, final Callback callback){
        RequestParams params;
        params = new RequestParams();
        params.add("token", APIController.KEY_SETTINGS_ONEMAP_TOKEN);
        params.add("routeStops",from.getLat()+","+from.getLon()+";"+destination.getLat()+","+destination.getLon());
        params.add("routemode","DRIVE");
        params.add("avoidERP","0");
        params.add("routeOption","shortest");

        GET(APIController.KEY_SETTINGS_URL_DIRECTION_API, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                parsJSON(callback, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                callback.failure("Fail to retrieve "+ Location.Category.HawkerCentres+" data.");
                callback.failure(responseString);
            }
        });
    }

    private void parsJSON(Callback callback, JSONObject response){

        direction= new Direction();
        ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
        try {
            JSONObject routes = response.getJSONObject("routes");
            JSONArray features = routes.getJSONArray("features");
            JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
            JSONArray path = geometry.getJSONArray("paths").getJSONArray(0);

            for(int i=0;i<path.length();i++){
                JSONArray JSONCoordinateSet = path.getJSONArray(i);
                Coordinate c = new Coordinate(JSONCoordinateSet.getDouble(0),JSONCoordinateSet.getDouble(1));
                coordinates.add(c);
            }
            direction.setCoordinateList(coordinates);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(direction.getCoordinateList().size()!=0){
            callback.success(direction,response);
        }
        else{
            callback.failure("Something went wrong here");
        }
    }
}
