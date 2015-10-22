package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.os.Bundle;

import org.json.JSONObject;

import sg.ntu.cz2002.controller.APIController;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.controller.Callback;
import sg.ntu.cz2002.controller.DirectionAPI;
import sg.ntu.cz2002.entity.Coordinate;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class DirectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
    }

    public void getDirectionData(Coordinate from, Coordinate destination){
        new DirectionAPI().getDirection(from, destination, new Callback() {
            @Override
            public void success(Object o, JSONObject response) {

            }

            @Override
            public void failure(String error) {

            }
        });
    }
}
