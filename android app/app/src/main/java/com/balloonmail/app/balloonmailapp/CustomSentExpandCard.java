package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CustomSentExpandCard extends CardExpand{

    Balloon balloon;

    private GoogleMap mMap;
    private Context context;
    private Bundle savedInstanceState;

    // Map Attributes
    LatLng sourceBalloon;
    HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap;

    //Use your resource ID for your inner layout
    public CustomSentExpandCard(Context context) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
    }
    public CustomSentExpandCard(Balloon balloon, Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_sent_expand);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;

        //will be replaced with the attributes of the balloon when received from the server
        sourceBalloon = new LatLng(30.065136, 31.278821);
        initializeHashMap();
    }

    private void initializeHashMap(){
        destinationsHashMap = new HashMap<>();
        ArrayList<LatLng> destinationsArrayList = new ArrayList<>();
        destinationsArrayList.add(new LatLng(-37.81319, 144.96298));
        destinationsArrayList.add(new LatLng(-33.87365, 151.20689));
        destinationsArrayList.add(new LatLng(-34.92873, 138.59995));
        destinationsArrayList.add(new LatLng(-31.95285, 115.85734));
        destinationsArrayList.add(new LatLng(51.471547, -0.460052));
        destinationsArrayList.add(new LatLng(33.936524, -118.377686));
        destinationsArrayList.add(new LatLng(40.641051, -73.777485));
        destinationsArrayList.add(new LatLng(-37.006254, 174.783018));
        destinationsHashMap.put(sourceBalloon, destinationsArrayList);
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

    private void setMapLocation(GoogleMap map) {
        // Add a marker for this item and set the camera
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceBalloon, 13f));
        map.addMarker(new MarkerOptions().position(sourceBalloon));

        for(int i=0; i<destinationsHashMap.get(sourceBalloon).size(); i++){
            map.addPolyline(new PolylineOptions().add(sourceBalloon, destinationsHashMap.get(sourceBalloon).get(i))
                    .color(Color.parseColor("#C1494E"))
                    .width(5)
                    .zIndex(i)
                    .geodesic(true)
            );
        }
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

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sourceBalloon)      // Sets the center of the map to Mountain View
                    .zoom(1)                   // Sets the zoom
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
