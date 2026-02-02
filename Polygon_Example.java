package com.atakmap.android.plugintemplate.plugin;

import android.graphics.Color;
import android.util.Log;

import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.Polyline;
import com.atakmap.coremap.maps.coords.GeoPoint;

import java.util.UUID;

public class Polygon_Example {

    private final MapView mapView;
    private final MapGroup mapGroup;

    public Polygon_Example(MapView mapView) {
        this.mapView = mapView;
        this.mapGroup = mapView.getRootGroup();//Probably a standard group this should be added to
    }

    public void drawShapes() {
        drawPolyline();
        drawPolygon();
    }

    private void drawPolyline() {
        String uid = UUID.randomUUID().toString();
        Polyline polyline = new Polyline(uid);

        GeoPoint[] points = new GeoPoint[] {
                new GeoPoint(-35.0, 138.566),
                new GeoPoint(-35.4, 138.7),
                new GeoPoint(-35.1, 137.2),

        };
        polyline.setPoints(points);

        polyline.setStrokeColor(Color.RED);

        if (mapGroup != null) {
            mapGroup.addItem(polyline);
            Log.d("Polygon_Example", "Polyline added to map");
        }
    }

    private void drawPolygon(){
        String uid = UUID.randomUUID().toString();
        Polyline polygon = new Polyline(uid);
        GeoPoint[] points = new GeoPoint[] {
                new GeoPoint(-35.8, 138.466),
                new GeoPoint(-35.4, 138.7),
                new GeoPoint(-35.1, 137.3),
                new GeoPoint(-35.8, 138.466)
        };

        polygon.setPoints(points);

        polygon.setStyle(Polyline.STYLE_FILLED_MASK | Polyline.STYLE_CLOSED_MASK | Polyline.STYLE_STROKE_MASK);


        polygon.setStrokeWeight(4d);
        polygon.setFillColor(Color.BLUE);
        polygon.setStrokeColor(Color.BLUE);

        if (mapGroup != null) {
            mapGroup.addItem(polygon);
            Log.d("Polygon_Example", "Polygon added to map");
        }

    }


}

