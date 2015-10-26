package sg.ntu.cz2002.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Polyline;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;

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
        Log.i("DIRECTION_FROM_LAT", intent.getStringExtra("DIRECTION_FROM_LAT"));
        Log.i("DIRECTION_FROM_LON TO", intent.getStringExtra("DIRECTION_FROM_LON"));
        Log.i("DIRECTION_TO_LAT", intent.getStringExtra("DIRECTION_TO_LAT"));
        Log.i("DIRECTION_TO_LON TO", intent.getStringExtra("DIRECTION_TO_LON"));

//        40470.770893904744,35694.43803585708,
//        Coordinate from = new Coordinate(Double.parseDouble(intent.getStringExtra("DIRECTION_FROM_LAT")),Double.parseDouble(intent.getStringExtra("DIRECTION_FROM_LON")));
        Coordinate from = new Coordinate(35537.38791087674,40674.31835960776);
        Coordinate to = new Coordinate(Double.parseDouble(intent.getStringExtra("DIRECTION_TO_LAT")),Double.parseDouble(intent.getStringExtra("DIRECTION_TO_LON")));
        getDirectionData(from,to);

        mBackBtn = (Button)findViewById(R.id.direction_back);
        mTitle = (TextView)findViewById(R.id.direction_title);

        mTitle.setText(intent.getStringExtra("LOCATION_NAME"));
        //TODO STYLE THE TEXTSIZE
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateBack();
            }
        });
    }

    private void navigateBack(){
        this.finish();
    }

    public void plotDirections(){
        if (graphicsLayer == null) graphicsLayer = new GraphicsLayer();
            Polyline line = new Polyline();

            line.startPath(direction.getCoordinateList().get(0).getLat(), direction.getCoordinateList().get(0).getLon());
            for (int i = 1; i < direction.getCoordinateList().size(); i++) {
//                    Log.i("ploting x"+i,direction.getCoordinateList().get(i).getLat()+"");
//                    Log.i("ploting Y"+i,direction.getCoordinateList().get(i).getLon()+"");
                line.lineTo(direction.getCoordinateList().get(i).getLat(), direction.getCoordinateList().get(i).getLon());
        }
        graphicsLayer.addGraphic(new Graphic(line, new SimpleLineSymbol(Color.RED,5f, SimpleLineSymbol.STYLE.SOLID)));
        mMapView.addLayer(graphicsLayer);

        //TODO add a pin at the start and end
    }

    public void getDirectionData(Coordinate from, Coordinate destination){
        new DirectionAPI().getDirection(from, destination, new Callback() {
            @Override
            public void success(Object o, JSONObject response) {
                direction = (Direction)o;
                plotDirections();
            }

            @Override
            public void failure(String error) {

            }
        });
    }
}
