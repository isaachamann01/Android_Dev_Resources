
package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.widget.Toast;

import com.atak.plugins.impl.PluginContextProvider;
import com.atak.plugins.impl.PluginLayoutInflater;
import com.atakmap.android.contact.ContactLocationView;
import com.atakmap.android.cotdetails.extras.ExtraDetailsManager;
import com.atakmap.android.ipc.AtakBroadcast;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;
import com.atakmap.coremap.maps.coords.GeoPoint;

import gov.tak.api.plugin.IPlugin;
import gov.tak.api.plugin.IServiceController;
import gov.tak.api.ui.IHostUIService;
import gov.tak.api.ui.Pane;
import gov.tak.api.ui.PaneBuilder;
import gov.tak.api.ui.ToolbarItem;
import gov.tak.api.ui.ToolbarItemAdapter;
import gov.tak.platform.marshal.MarshalManager;
import com.atakmap.map.layer.opengl.GLLayerFactory;

public class PluginTemplate implements IPlugin {

    IServiceController serviceController;
    Context pluginContext;
    IHostUIService uiService;
    ToolbarItem toolbarItem;
    Pane templatePane;
    private CarsCotProcessor carsProcessor;

    private Polygon_Example polygonExample;

    private AoCreationTool aoCreationTool;

    private NodeSelection nodeSelection;
    
    private CoverageMapOverlay coverageMapOverlay;
    private CoverageMapLayer coverageLayer;

    public PluginTemplate(IServiceController serviceController) {
        this.serviceController = serviceController;
        final PluginContextProvider ctxProvider = serviceController
                .getService(PluginContextProvider.class);
        if (ctxProvider != null) {
            pluginContext = ctxProvider.getPluginContext();
            pluginContext.setTheme(R.style.ATAKPluginTheme);
        }

        // obtain the UI service
        uiService = serviceController.getService(IHostUIService.class);

        // create the button
        toolbarItem = new ToolbarItem.Builder(
                pluginContext.getString(R.string.app_name),
                MarshalManager.marshal(
                        pluginContext.getResources().getDrawable(R.drawable.ic_launcher),
                        android.graphics.drawable.Drawable.class,
                        gov.tak.api.commons.graphics.Bitmap.class))
                .setListener(new ToolbarItemAdapter() {
                    @Override
                    public void onClick(ToolbarItem item) {
                        showPane();
                    }
                })
                .build();




        carsProcessor = new CarsCotProcessor(MapView.getMapView());
        carsProcessor.register();

        polygonExample = new Polygon_Example(MapView.getMapView());

        aoCreationTool = new AoCreationTool(MapView.getMapView());

        nodeSelection = new NodeSelection(MapView.getMapView());

        // Setup Coverage Map Overlay
        coverageMapOverlay = new CoverageMapOverlay(MapView.getMapView(), pluginContext);
        MapView.getMapView().getMapOverlayManager().addOverlay(coverageMapOverlay);

        // Register the GL renderer for our custom layer
        GLLayerFactory.register(GLCoverageMapLayer.SPI);
        
        // Register the receiver for tool callbacks
        AtakBroadcast.getInstance().registerReceiver(aoCreationTool, new AtakBroadcast.DocumentedIntentFilter(AoCreationTool.ACTION_SHAPE_CREATED));

    }

    @Override
    public void onStart() {
        if (uiService == null)
            return;
        uiService.addToolbarItem(toolbarItem);
    }

    @Override
    public void onStop() {
        if (uiService == null)
            return;
        uiService.removeToolbarItem(toolbarItem);


        if (coverageMapOverlay != null) {
            MapView.getMapView().getMapOverlayManager().removeOverlay(coverageMapOverlay);
        }


        AtakBroadcast.getInstance().unregisterReceiver(aoCreationTool);

        if (carsProcessor != null) {
            carsProcessor.unregister();
        }
    }

    private void showPane() {
        if(templatePane == null) {
            // Inflate the view so we can find the button inside it
            android.view.View v = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null);

            // Find the button and set the click listener
            v.findViewById(R.id.create_region_btn).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    if (aoCreationTool != null) {
                        aoCreationTool.start();
                    }
                }
            });

            v.findViewById(R.id.get_ao_points_btn).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    if (carsProcessor != null) {
                        String points = carsProcessor.getAoPoints();
                        if (points != null) {
                            Log.d("PluginTemplate", "AO Points Found: " + points);
                            Toast.makeText(pluginContext, "AO Points logged to LogCat", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(pluginContext, "No AO Points found on map", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            v.findViewById(R.id.optimise_btn).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    if (nodeSelection != null) {
                        nodeSelection.startContactSelection();
                    }
                }
            });

            v.findViewById(R.id.update_coverage_btn).setOnClickListener(new android.view.View.OnClickListener() {
                @Override
                public void onClick(android.view.View view) {
                    GeoPoint selfLoc = MapView.getMapView().getSelfMarker().getPoint();
                    if (coverageLayer == null) {
                        coverageLayer = new CoverageMapLayer(pluginContext, "Coverage Layer", "img.png", selfLoc);
                        MapView.getMapView().addLayer(MapView.RenderStack.MAP_SURFACE_OVERLAYS, coverageLayer);
                        Toast.makeText(pluginContext, "Coverage Layer Added at Self Location", Toast.LENGTH_SHORT).show();
                    } else {
                        // Toggle visibility or update
                        coverageLayer.setVisible(!coverageLayer.isVisible());
                        Toast.makeText(pluginContext, "Coverage Layer " + (coverageLayer.isVisible() ? "Visible" : "Hidden"), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            templatePane = new PaneBuilder(v)
                    .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                    .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, 0.5D)
                    .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, 0.5D)
                    .build();
        }

        if(!uiService.isPaneVisible(templatePane)) {
            uiService.showPane(templatePane, null);
        }
    }
}
