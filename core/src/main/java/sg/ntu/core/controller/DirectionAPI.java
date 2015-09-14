package sg.ntu.core.controller;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import sg.ntu.core.Core;
import sg.ntu.core.entity.Coordinate;
import sg.ntu.core.entity.Direction;
import sg.ntu.core.entity.Location;

/**
 * Created by Moistyburger on 7/9/15.
 */
public class DirectionAPI {
    private Direction direction;
    private AsyncHttpClient client = new AsyncHttpClient();

    public void getDirection(Coordinate from,Coordinate destination, final Core.Callback callback){
        RequestParams params;
    }
}
