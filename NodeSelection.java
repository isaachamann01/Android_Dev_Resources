package com.atakmap.android.plugintemplate.plugin;

import android.widget.Toast;

import com.atakmap.android.hierarchy.HierarchyListFilter;
import com.atakmap.android.contact.IndividualContact;
import com.atakmap.android.hierarchy.HierarchyListItem;
import com.atakmap.android.importexport.send.TAKContactSender;
import com.atakmap.android.data.URIContentRecipient;
import com.atakmap.android.data.URIContentSender;
import com.atakmap.android.maps.MapItem;
import com.atakmap.android.maps.MapView;
import com.atakmap.coremap.log.Log;

import java.util.List;

public class NodeSelection extends TAKContactSender {

    private static final String TAG = "NodeSelection";

    public NodeSelection(MapView mapView) {
        super(mapView);
    }

     // Triggers the ATAK contact selection menu.
    public void startContactSelection() {

        //Assuming that only contacts with Cars data are available for optimisation
        HierarchyListFilter carsFilter = new HierarchyListFilter(null) {
            @Override
            public boolean accept(HierarchyListItem item){

                //Only show indivudal team members - may want to change
                if(!(item instanceof IndividualContact)){
                    Log.d(TAG, "Item is not a contact");
                    return false;
                }

                //Get marker details for contact
                IndividualContact contact = (IndividualContact) item;
                MapItem mapItem = contact.getMapItem();

                //Filter based on metadata tag
                return mapItem != null && mapItem.hasMetaValue("CarsPlugin.carsModel");

            }
        };


        String dummyUri = "plugin://optimise";

        this.selectRecipients(dummyUri, new URIContentRecipient.Callback() {
            @Override
            public void onSelectRecipients(URIContentSender sender, String uri, List<? extends URIContentRecipient> recipients) {
                if (recipients == null || recipients.isEmpty()) {
                    Log.d(TAG, "No contacts selected.");
                    return;
                }

                Log.d(TAG, "Selected " + recipients.size() + " contacts for optimisation.");

                // Process the contacts
                for (URIContentRecipient contact : recipients) {
                    Log.d(TAG, "Recipient: " + contact.getName() + " (UID: " + contact.getUID() + ")");
                }

                Toast.makeText(_mapView.getContext(), "Optimising for " + recipients.size() + " contacts", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


