package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.atakmap.android.maps.MetaShape;
import com.atakmap.android.menu.PluginMenuParser;
import com.atakmap.coremap.maps.coords.GeoBounds;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.coremap.maps.coords.GeoPointMetaData;
import com.atakmap.coremap.maps.coords.MutableGeoBounds;
import com.atakmap.map.layer.AbstractLayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class CoverageMapLayer extends AbstractLayer {
    public static final String TAG = "CarsCoverageMap";

    protected int[] layerARGB;
    protected int layerWidth;
    protected int layerHeight;
    protected GeoPoint upperLeft;
    protected GeoPoint upperRight;
    protected GeoPoint lowerLeft;
    protected GeoPoint lowerRight;

    private final Context pluginContext;
    private final MetaShape metaShape;

    public CoverageMapLayer(Context plugin, String name, String assetPath, GeoPoint center){
        super(name);
        this.pluginContext = plugin;

        //Set initial bounds
        this.upperLeft = GeoPoint.createMutable();
        this.upperRight = GeoPoint.createMutable();
        this.lowerLeft = GeoPoint.createMutable();
        this.lowerRight = GeoPoint.createMutable();

        Bitmap bitmap = null;
        try (InputStream is = plugin.getAssets().open(assetPath)) {
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load asset: " + assetPath, e);
        }

        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null for asset: " + assetPath);
            layerARGB = new int[0];
            metaShape = null;
            return;
        }

        double offset = 0.005; 
        upperLeft.set(center.getLatitude() + offset, center.getLongitude() - offset);
        upperRight.set(center.getLatitude() + offset, center.getLongitude() + offset);
        lowerLeft.set(center.getLatitude() - offset, center.getLongitude() - offset);
        lowerRight.set(center.getLatitude() - offset, center.getLongitude() + offset);

        layerWidth = bitmap.getWidth();
        layerHeight = bitmap.getHeight();

        Log.d(TAG, "decode asset: " + assetPath + " " + layerWidth + " " + layerHeight);

        layerARGB = new int[layerHeight * layerWidth];
        bitmap.getPixels(layerARGB, 0, layerWidth, 0, 0, layerWidth, layerHeight);

        //Metashape allows layer to be treated as a map object
        metaShape = new MetaShape(UUID.randomUUID().toString()) {
            @Override
            public GeoPointMetaData[] getMetaDataPoints(){
                return GeoPointMetaData.wrap(CoverageMapLayer.this.getPoints());
            }
            @Override
            public GeoPoint[] getPoints(){
                return CoverageMapLayer.this.getPoints();
            }

            @Override
            public GeoBounds getBounds(MutableGeoBounds bounds){
                return CoverageMapLayer.this.getBounds();
            }
        };

        metaShape.setMetaString("callsign", name);
        metaShape.setMetaString("shapeName", name);
        metaShape.setType("Coverage_Map_Layer");
        metaShape.setMetaString("menu", PluginMenuParser.getMenu(pluginContext, "menus/layer_menu.xml"));
        bitmap.recycle();

    }

    public GeoBounds getBounds(){
        return GeoBounds.createFromPoints(getPoints());
    }

    public GeoPoint[] getPoints() {
        return new GeoPoint[]{upperLeft, upperRight, lowerRight, lowerLeft};
    }

    public MetaShape getMetaShape(){
        return metaShape;
    }

}
