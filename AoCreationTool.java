package com.atakmap.android.plugintemplate.plugin;

import static com.atakmap.android.plugintemplate.plugin.CarsCotProcessor.KEY_AO_POINTS;
import static com.atakmap.android.plugintemplate.plugin.CarsCotProcessor.KEY_IS_AO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Polyline;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoPoint;

import java.util.Collection;

public class AoCreationTool extends BroadcastReceiver {

    private static final String TAG = "AoCreationTool";
    public static final String ACTION_SHAPE_CREATED = "com.atakmap.android.plugintemplate.SHAPE_CREATED";
    private final MapView mapView;

    public AoCreationTool(MapView mapView) {
        this.mapView = mapView;
    }

    public void start() {
        Log.d(TAG, "Requesting ShapeCreationTool start");
        
        Intent intent = new Intent("com.atakmap.android.maps.toolbar.BEGIN_TOOL");
        
        intent.putExtra("tool", "com.atakmap.android.drawing.SHAPE_TOOL");
        
        intent.putExtra("drawingGroup", "Drawing Objects");
        
        Intent callback = new Intent(ACTION_SHAPE_CREATED);
        intent.putExtra("callback", callback);
        
        AtakBroadcast.getInstance().sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_SHAPE_CREATED.equals(action)) {
            String uid = intent.getStringExtra("uid");
            Log.d(TAG, "Shape creation callback received for UID: " + uid);
            
            if (uid != null) {
                //Remove existing AO from local map
                removeExistingAo();

                // Get generated item based on returned UID
                MapItem item = mapView.getRootGroup().deepFindUID(uid);
                if (item instanceof Polyline) {
                     Polyline aoPolyline = (Polyline) item;

                    //Meta key so the internal processor knows its an AO
                    aoPolyline.setMetaString(KEY_IS_AO, "true");

                    //Display options for AO (TBD)
                    aoPolyline.setTitle("Area of Operation");
                    aoPolyline.setStrokeColor(Color.GREEN);
                    aoPolyline.setStrokeWeight(4);
                    aoPolyline.setFillColor(Color.GREEN);
                    aoPolyline.setFillAlpha(50);
                    aoPolyline.setStyle(Polyline.STYLE_FILLED_MASK | Polyline.STYLE_CLOSED_MASK | Polyline.STYLE_STROKE_MASK);
                    aoPolyline.setStrokeStyle(3); //Outlined style

                    //Extract point data and apply to AO metadata
                    GeoPoint[] aoPoints = aoPolyline.getPoints();
                    StringBuilder sb = new StringBuilder();
                    for (GeoPoint point : aoPoints) {
                        sb.append(point.getLatitude()).append(",").append(point.getLongitude()).append(";");
                    }
                    aoPolyline.setMetaString(KEY_AO_POINTS,sb.toString());
                    Log.d(TAG, "Setting AO points to MetaData: " + sb.toString());

                } else if (item != null) {
                    Log.w(TAG, "Item found but not a Polyline: " + item.getClass().getName());
                } else {
                    Log.w(TAG, "Failed to find MapItem for UID: " + uid);
                }
            }
        }
    }
    private void removeExistingAo() {
        Collection<MapItem> existingItems = mapView.getRootGroup().deepFindItems(KEY_IS_AO, "true");
        if (!existingItems.isEmpty()) {
            Log.d(TAG, "Removing " + existingItems.size() + " existing AO shape(s)");
            for (MapItem oldItem : existingItems) {
                MapGroup parent = oldItem.getGroup();
                if (parent != null) {
                    parent.removeItem(oldItem);
                }
            }
        }
    }
}
