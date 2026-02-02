package com.atakmap.android.plugintemplate.plugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.atakmap.android.hierarchy.HierarchyListFilter;
import com.atakmap.android.hierarchy.HierarchyListItem;
import com.atakmap.android.hierarchy.action.GoTo;
import com.atakmap.android.hierarchy.action.Visibility;
import com.atakmap.android.hierarchy.action.Search;

import com.atakmap.android.hierarchy.action.Visibility2;
import com.atakmap.android.hierarchy.items.AbstractHierarchyListItem2;
import com.atakmap.android.hierarchy.items.MapItemUser;
import com.atakmap.android.maps.DeepMapItemQuery;
import com.atakmap.android.maps.DefaultMapGroup;
import com.atakmap.android.maps.MapGroup;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.android.maps.MetaShape;
import com.atakmap.android.menu.MapMenuReceiver;
import com.atakmap.android.menu.MenuLayoutWidget;
import com.atakmap.android.overlay.AbstractMapOverlay2;
import com.atakmap.android.util.ATAKUtilities;
import com.atakmap.coremap.maps.coords.GeoBounds;
import com.atakmap.coremap.maps.coords.GeoPoint;
import com.atakmap.map.layer.Layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class CoverageMapOverlay extends AbstractMapOverlay2{

    private static final String TAG = "CoverageMapOverlay";

    private final MapView mapView;
    private final Context plugin;

    private final CoverageMapDeepMapItemQuery query;
    private final DefaultMapGroup group;

    private CoverageMapListModel listModel;

    public CoverageMapOverlay(MapView mapView, Context plugin){
        this.mapView = mapView;
        this.plugin = plugin;
        this.query = new CoverageMapDeepMapItemQuery();
        this.group = new DefaultMapGroup("Coverage Map Group");
        group.setMetaBoolean("addToObjList", false);
    }

    @Override
    public String getIdentifier(){
        return TAG;
    }

    @Override
    public String getName(){
        return plugin.getString((R.string.app_name));
    }

    public MapGroup getRootGroup(){
        return group;
    }

    @Override
    public DeepMapItemQuery getQueryFunction(){
        return query;
    }

    @Override
    public HierarchyListItem getListModel(BaseAdapter adapter,
                                          long capabilities, HierarchyListFilter prefFilter){
        if (listModel ==null) listModel = new CoverageMapListModel();
        listModel.refresh(adapter,prefFilter);
        return listModel;
    }

    private List<CoverageMapLayer> getLayers(){
        List<CoverageMapLayer> ret = new ArrayList<>();
        List<Layer> layers = mapView.getLayers(MapView.RenderStack.MAP_SURFACE_OVERLAYS);
        for( Layer l : layers){
            if (l instanceof CoverageMapLayer){
                CoverageMapLayer el = (CoverageMapLayer) l;
                MetaShape shape = el.getMetaShape();
                if(shape.getGroup()==null) group.addItem(shape);
                ret.add(el);

            }
        }
        return ret;
    }

    public CoverageMapLayer findLayer(String uid){
        for (CoverageMapLayer l : getLayers()){
            if (l.getMetaShape().getUID().equals(uid)) return l;
        }
        return null;
    }

    public class CoverageMapListModel extends AbstractHierarchyListItem2 implements Search, Visibility2, View.OnClickListener{
        private final static String TAG = "CoverageMapListModel";
        private View header, footer;

        public CoverageMapListModel(){
            this.asyncRefresh = true;
        }
        @Override
        public String getTitle(){
            return CoverageMapOverlay.this.getName();
        }

        @Override
        public String getIconUri() {
            return "android.resource://" + plugin.getPackageName()
                    + "/" + R.drawable.ic_launcher;
        }

        public int getPreferredListIndex(){
            return 5;
        }

        @Override
        public int getDescendantCount(){
            return 0;
        }

        @Override
        public Object getUserObject(){
            return this;
        }

        @Override
        public View getExtraView() {
            return null;
        }

        @Override
        public View getHeaderView() {
            if (header == null) {
                header = LayoutInflater.from(plugin).inflate(
                        R.layout.overlay_header, mapView, false);
                header.findViewById(R.id.header_button)
                        .setOnClickListener(this);
            }
            return header;
        }

        @Override
        public View getFooterView() {
            if (footer == null) {
                footer = LayoutInflater.from(plugin).inflate(
                        R.layout.overlay_footer, mapView, false);
                footer.findViewById(R.id.footer_button)
                        .setOnClickListener(this);
            }
            return footer;
        }

        @Override
        public void refreshImpl() {
            List<HierarchyListItem> filtered = new ArrayList<>();
            List<CoverageMapLayer> layers = getLayers();
            for (CoverageMapLayer l : layers) {
                LayerHierarchyListItem item = new LayerHierarchyListItem(l);
                if (this.filter.accept(item))
                    filtered.add(item);
            }
            // Sort
            sortItems(filtered);

            // Update
            updateChildren(filtered);
        }

        @Override
        public void dispose() {
            disposeChildren();
        }

        @Override
        public boolean hideIfEmpty() {
            return true;
        }

        @Override
        public boolean isMultiSelectSupported() {
            return false;
        }

        @Override
        public boolean setVisible(boolean visible) {
            List<Visibility> actions = getChildActions(Visibility.class);
            boolean ret = !actions.isEmpty();
            for (Visibility del : actions)
                ret &= del.setVisible(visible);
            return ret;
        }

        @Override
        public Set<HierarchyListItem> find(String searchTerms) {
            searchTerms = searchTerms.toLowerCase();
            Set<HierarchyListItem> results = new HashSet<>();
            List<HierarchyListItem> items = getChildren();
            for (HierarchyListItem item : items) {
                if (item.getTitle().toLowerCase().contains(searchTerms))
                    results.add(item);
            }
            return results;
        }

        @Override
        public void onClick(View v) {
            if (v instanceof Button)
                Toast.makeText(mapView.getContext(),
                        ((Button) v).getText(),
                        Toast.LENGTH_LONG).show();
        }
    }

    private class LayerHierarchyListItem extends AbstractHierarchyListItem2
            implements Visibility, GoTo, MapItemUser {

        private final CoverageMapLayer layer;

        LayerHierarchyListItem(CoverageMapLayer layer) {
            this.layer = layer;
        }

        @Override
        public String getTitle() {
            if (layer != null) {
                return layer.getName();
            }
            return "";
        }

        @Override
        public String getIconUri() {
            return "android.resource://" + mapView.getContext()
                    .getPackageName() + "/"
                    + com.atakmap.app.R.drawable.ic_overlay_gridlines;
        }

        @Override
        public Object getUserObject() {
            if (layer != null) {
                return layer;
            }
            return null;
        }

        @Override
        public boolean isChildSupported() {
            return false;
        }

        @Override
        public int getDescendantCount() {
            return 0;
        }

        @Override
        public void refreshImpl() {
        }

        @Override
        public boolean hideIfEmpty() {
            return false;
        }

        @Override
        public boolean setVisible(boolean visible) {
            if (layer != null) {
                if (visible != layer.isVisible()) {
                    layer.setVisible(visible);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean isVisible() {
            if (layer != null) {
                return layer.isVisible();
            }
            return false;
        }

        @Override
        public MapItem getMapItem() {
            if (layer != null) {
                return layer.getMetaShape();
            }
            return null;
        }

        @Override
        public boolean goTo(boolean select) {
            if (layer != null) {
                ATAKUtilities.scaleToFit(mapView, layer.getPoints(),
                        mapView.getWidth(), mapView.getHeight());
                if (select) {
                    MenuLayoutWidget mw = MapMenuReceiver.getMenuWidget();
                    if (mw != null) {
                        mw.openMenuOnItem(layer.getMetaShape());
                        return true;
                    }
                }
            }
            return false;
        }
    }



    private class CoverageMapDeepMapItemQuery implements DeepMapItemQuery{


        public MapItem deepHitTest(int xpos, int ypos, GeoPoint point, MapView view){
            for (CoverageMapLayer l : getLayers()){
                if(l.isVisible() && l.getBounds().contains(point)) return l.getMetaShape();
            }
            return null;
        }

        @Override
        public SortedSet<MapItem> deepHitTestItems(int xpos, int ypos, GeoPoint point, MapView view){
            SortedSet<MapItem> ret = new TreeSet<>(
                    MapItem.ZORDER_HITTEST_COMPARATOR);
            for (CoverageMapLayer l : getLayers()) {
                if (l.isVisible() && l.getBounds().contains(point)) ret.add(l.getMetaShape());
            }
            return ret;

        }

        @Override
        public MapItem deepFindItem(Map<String, String> map) {
            return null;
        }

        @Override
        public List<MapItem> deepFindItems(Map<String, String> map) {
            return Collections.emptyList();
        }

        @Override
        public MapItem deepFindClosestItem(GeoPoint geoPoint, double v, Map<String, String> map) {
            return null;
        }

        @Override
        public Collection<MapItem> deepFindItems(GeoPoint geoPoint, double v, Map<String, String> map) {
            return Collections.emptyList();
        }

        @Override
        public Collection<MapItem> deepFindItems(GeoBounds bounds,
                                                 Map<String, String> metadata){
            SortedSet<MapItem> ret = new TreeSet<>(
                    MapItem.ZORDER_HITTEST_COMPARATOR);
            for (CoverageMapLayer l : getLayers()){
                if (l.isVisible() && l.getBounds().intersects(bounds)) ret.add(l.getMetaShape());
            }
            return ret;
        }

    }


}
