package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;

import org.json.JSONObject;

import sg.ntu.cz2002.controller.APIController;
import sg.ntu.cz2002.R;
import sg.ntu.cz2002.controller.Callback;
import sg.ntu.cz2002.controller.DirectionAPI;
import sg.ntu.cz2002.entity.Coordinate;
import sg.ntu.cz2002.entity.Direction;

/**
 * Created by Lee Kai Quan on 8/9/15.
 */

public class DirectionActivity extends Activity {

    private MapView mMapView = null;
    GraphicsLayer graphicsLayer;
    private Direction direction;

    Button mBackBtn;
    TextView mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.enableWrapAround(true);
        ArcGISRuntime.setClientId("j9r0J2JIy8FFFfB8");
        mMapView.addLayer(new ArcGISTiledMapServiceLayer("http://www.onemap.sg/ArcGIS/rest/services/BASEMAP/MapServer"));


        Intent intent = getIntent();
        Log.i("DIRECTION FROM", intent.getStringExtra("DIRECTION_FROM"));
        Log.i("DIRECTION TO", intent.getStringExtra("DIRECTION_TO"));



        mBackBtn = (Button)findViewById(R.id.direction_back);
        mTitle = (TextView)findViewById(R.id.direction_title);
    }

    public void plotDirections(){
        if (graphicsLayer == null) graphicsLayer = new GraphicsLayer();
            Polyline line = new Polyline();

            line.startPath(direction.getCoordinateList().get(0).getLat(), direction.getCoordinateList().get(0).getLon());
            for (int i = 1; i < direction.getCoordinateList().size(); i++) {
    //                Log.i("ploting x"+i,BaseActivity.directions.get(i).getX()+"");
    //                Log.i("ploting Y"+i,BaseActivity.directions.get(i).getY()+"");
                line.lineTo(direction.getCoordinateList().get(i).getLat(), direction.getCoordinateList().get(i).getLon());
        }
        graphicsLayer.addGraphic(new Graphic(line, new SimpleFillSymbol(Color.RED)));
        mMapView.addLayer(graphicsLayer);
    }

    public void getDirectionData(Coordinate from, Coordinate destination){
        new DirectionAPI().getDirection(from, destination, new Callback() {
            @Override
            public void success(Object o, JSONObject response) {
                direction = (Direction)o;
            }

            @Override
            public void failure(String error) {

            }
        });
    }
}
