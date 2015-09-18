package sg.ntu.cz2002.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import org.json.JSONObject;

import sg.ntu.cz2002.Core;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.entity.Coordinate;


public class DirectionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
    }

    public void getDirectionData(Coordinate from, Coordinate destination){
        Core.getInstance().getDirectionAPI().getDirection(from,destination, new Core.Callback() {
            @Override
            public void success(Object o, JSONObject response) {

            }

            @Override
            public void failure(String error) {

            }
        });
    }
}
