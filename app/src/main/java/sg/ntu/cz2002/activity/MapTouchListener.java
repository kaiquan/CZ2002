package sg.ntu.cz2002.activity;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;


/**
 * Created by Moistyburger on 24/10/15.
 */
class MapTouchListener extends MapOnTouchListener {
    ScrollView sv;
    Context c;
    public MapTouchListener(Context c, MapView m)
    {
        super(c, m);
    }
    public void setScrollview(ScrollView sv){
        this.sv=sv;
    }
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                sv.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                sv.requestDisallowInterceptTouchEvent(false);
                break;
        }        super.onTouch(v, event);
        return true;
    }
}
