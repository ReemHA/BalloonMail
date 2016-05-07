package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CustomSentExpandCard extends CardExpand implements OnMapReadyCallback{

    private GoogleMap mMap;
    private Context context;
    private Bundle savedInstanceState;

    MapView mapView;

    private static final String MAP_FRAGMENT_TAG = "map";

    //Use your resource ID for your inner layout
    public CustomSentExpandCard(Context context) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
    }
    public CustomSentExpandCard(Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view){
        View row = view;
        SentExpandCardViewHolder holder;

        if (row == null){ return; }

        holder = new SentExpandCardViewHolder();
        //holder.title = (TextView) view.findViewById(R.id.e_textView);
        holder.mapView = (MapView) view.findViewById(R.id.row_map);

        holder.initializeMapView();

        if (holder.map != null) {
            // The map is already ready to be used
            setMapLocation(holder.map);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    private static void setMapLocation(GoogleMap map) {
        // Add a marker for this item and set the camera
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.937795, 116.387224), 13f));
        map.addMarker(new MarkerOptions().position(new LatLng(39.937795, 116.387224)));

        // Set the map type back to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    class SentExpandCardViewHolder implements OnMapReadyCallback {

        MapView mapView;

        GoogleMap map;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            /*NamedLocation data = (NamedLocation) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }*/
            setMapLocation(map);

            map.getUiSettings().setZoomGesturesEnabled(false); //gestures not enabled in lite mode
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(37.4, -122.1))      // Sets the center of the map to Mountain View
                    .zoom(5)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-33.87365, 151.20689), 10));

        }

        /**
         * Initialises the MapView by calling its lifecycle methods.
         */
        public void initializeMapView() {
            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(savedInstanceState);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
                mapView.setClickable(false);
            }
        }

    }

}
