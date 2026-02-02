package com.atakmap.android.plugintemplate.plugin;

import android.content.Intent;

import com.atakmap.android.cot.CotMapComponent;
import com.atakmap.android.cot.detail.CotDetailHandler;
import com.atakmap.android.cot.detail.CotDetailManager;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.comms.CommsMapComponent;
import com.atakmap.comms.ReportingRate;
import com.atakmap.coremap.cot.event.CotDetail;
import com.atakmap.coremap.cot.event.CotEvent;
import com.atakmap.coremap.log.Log;

import java.util.Collection;

public class CarsCotProcessor {
    private  static final String TAG = "CarsCotProcessor";

    public static final String CARS_DETAIL_FILTER_TAG = "__CarsData";

    // Plugin specific prefix to prevent interference of metadata from other plugins
    public static final String KEY_IS_AO = "CarsPlugin.isAO";
    public static final String KEY_AO_POINTS = "CarsPlugin.AoPoints";



    private final MapView mapView;
    private CotDetailHandler carsDetail;

    public CarsCotProcessor(MapView mapView) {
        this.mapView = mapView;
    }


    /*
    Handles bidirectional conversion between items metadata and COT detail.
    As metadata is local to the ATAK instance it is lost when shared over the network
     */
    public void register() {
        CotDetailManager.getInstance().registerHandler(carsDetail = new CotDetailHandler(CARS_DETAIL_FILTER_TAG) {
            @Override
            public CommsMapComponent.ImportResult toItemMetadata(MapItem item, CotEvent event, CotDetail detail) {
                Log.d(TAG, "Parsing car data from COT: " + detail);

                //User receives AO region via network and assigns incoming COT to object Metadata
                if(detail.getAttribute(KEY_IS_AO)!= null){

                    //Handle removing existing AO if already present
                    Collection<MapItem> existing = mapView.getRootGroup().deepFindItems(KEY_IS_AO, "true");
                    for (MapItem oldItem : existing) {
                        // Don't remove new AO item
                        if (oldItem != item) {
                            oldItem.removeFromGroup();
                        }
                    }
                    //Set metadata from incoming COT to metadata
                    item.setMetaString(KEY_IS_AO, detail.getAttribute(KEY_IS_AO));
                    item.setMetaString(KEY_AO_POINTS, detail.getAttribute(KEY_AO_POINTS));
                    Log.d(TAG, "Received AO via sharing. Metadata saved to item.");
                }
                return CommsMapComponent.ImportResult.SUCCESS;
            }

            @Override
            public boolean toCotDetail(MapItem item, CotEvent event, CotDetail detail) {

                //Package AO metadata into AO COT when shared by user over the network
                if (item.hasMetaValue(KEY_IS_AO)){
                    CotDetail AoDetail = new CotDetail(CARS_DETAIL_FILTER_TAG);
                    AoDetail.setAttribute(KEY_IS_AO, "true");
                    AoDetail.setAttribute(KEY_AO_POINTS, item.getMetaString(KEY_AO_POINTS, ""));
                    detail.addChild(AoDetail);
                    Log.d(TAG, "Sending AO via sharing. Metadata saved to COT.");


                }
                return true;
            }

        });
    }
    protected void unregister(){
        CotDetailManager.getInstance().unregisterHandler(carsDetail);
    }

    //Allows user specific data to be assigned to the self marker and attached to the regular SA message.
    //E.g radio capability etc
    public void sendInternal(CotDetail detail) {

        //Manually update the self marker - Note need to handle specific parsing for detail type
        if (carsDetail != null){
            carsDetail.toItemMetadata(mapView.getSelfMarker(),null,detail);
        }

        //Attach to SA message --MAY NOT BE IDEAL HERE FOR DEMO OF FUNCTION
        //Would allow every teammate to see each others radio equipment etc
        Log.d(TAG, "Dispatching car data internally " );
        CotMapComponent.getInstance().addAdditionalDetail(detail.getElementName(),detail);

        //Trigger immediate update
        AtakBroadcast.getInstance().sendBroadcast(new Intent(ReportingRate.REPORT_LOCATION).putExtra("reason","detail update for Cars"));

    }

    //Searches for AO item and returns the points as a string, not parsed to the self marker to ensure up to date data
    public String getAoPoints() {
        Collection<MapItem> items = mapView.getRootGroup().deepFindItems(KEY_IS_AO, "true");
        if (items.isEmpty()) {
            Log.w(TAG, "No AO item found in map.");
            return null;
        }

        // Return the first match  - Will need adjusting for multiple Ao
        MapItem aoItem = items.iterator().next();
        String points = aoItem.getMetaString(KEY_AO_POINTS, null);
        
        if (points == null) {
            Log.w(TAG, "AO item found but points data missing" );
        }
        Log.d(TAG, "Returning AO points: " + points);
        
        return points;
    }

}
