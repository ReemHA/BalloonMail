package com.balloonmail.app.balloonmailapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MailDetailsAndMapActivityFragment extends Fragment implements OnMapReadyCallback{

    Balloon balloon;
    View rootView;
    GoogleMap map;

    public MailDetailsAndMapActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView != null)
            return rootView;

        rootView = inflater.inflate(R.layout.fragment_mail_details_and_map, container, false);

        Bundle bundle = getArguments();
        if(bundle != null){
            balloon = (Balloon)bundle.getSerializable("balloonDetailFragment");

            TextView text = (TextView)rootView.findViewById(R.id.sentBalloonTextTv);
            text.setText(balloon.getText());

            TextView refill = (TextView)rootView.findViewById(R.id.refillTv_details);
            refill.setText(String.valueOf(balloon.getRefills()) + " refills");

            //TextView reach = (TextView)rootView.findViewById(R.id.reachTv_details);
            //reach.setText(String.valueOf(balloon.getReach()) + " reach");

            TextView creep = (TextView)rootView.findViewById(R.id.creepTv_details);
            creep.setText(String.valueOf(balloon.getCreeps()) + " creeps");

            //Map
            SupportMapFragment mapFragment =
                    (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_detail_fragment);

            mapFragment.getMapAsync(this);

        }

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sourceBalloon = balloon.getSourceBalloon();
        HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap = balloon.getDestinationsHashMap();

        // TODO static data will be removed
        LatLng d1 = new LatLng(-37.81319, 144.96298);
        LatLng d2 = new LatLng(-33.87365, 151.20689);
        LatLng d3 = new LatLng(-34.92873, 138.59995);
        LatLng d4 = new LatLng(-31.95285, 115.85734);
        LatLng d5 = new LatLng(51.471547, -0.460052);
        LatLng d6 = new LatLng(33.936524, -118.377686);
        LatLng d7 = new LatLng(40.641051, -73.777485);
        LatLng d8 = new LatLng(-37.006254, 174.783018);

        destinationsHashMap = new HashMap<>();
        ArrayList<LatLng> destinationsArrayList = new ArrayList<>();
        //destinationsArrayList.add(d1);
        destinationsArrayList.add(d2);
        destinationsArrayList.add(d3);
        destinationsArrayList.add(d4);
        destinationsArrayList.add(d5);
        destinationsArrayList.add(d6);
        destinationsArrayList.add(d7);
        destinationsArrayList.add(d8);

        ArrayList<LatLng> destinationsRefilledArrayList = new ArrayList<>();
        destinationsRefilledArrayList.add(d1);
        destinationsHashMap.put(sourceBalloon, destinationsArrayList);
        destinationsHashMap.put(d7, destinationsRefilledArrayList);
        //end of static data

        if(sourceBalloon != null){
            map.addMarker(new MarkerOptions()
                    .position(sourceBalloon)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_source_balloon)));

            if(destinationsHashMap != null){
                for(int i=0; i<destinationsHashMap.get(sourceBalloon).size(); i++){
                    LatLng destination = destinationsHashMap.get(sourceBalloon).get(i);
                    map.addPolyline(new PolylineOptions().add(sourceBalloon, destination)
                            .color(Color.parseColor("#D86C74"))
                            .width(5)
                            .zIndex(i)
                            .geodesic(true)
                    );
                    map.addMarker(new MarkerOptions()
                            .position(destination)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destination_light)));

                    //check if destination has another refills
                    if(destinationsHashMap.get(destination) != null){
                        for(int j=0; j<destinationsHashMap.get(destination).size(); j++){
                            LatLng refilledDestination = destinationsHashMap.get(destination).get(j);
                            map.addPolyline(new PolylineOptions().add(destination, refilledDestination)
                                    .color(Color.parseColor("#A52831"))
                                    .width(4)
                                    .zIndex(i)
                                    .geodesic(true)
                            );
                            map.addMarker(new MarkerOptions()
                                    .position(refilledDestination)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destination)));
                        }
                    }
                }
            }

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sourceBalloon)      // Sets the center of the map to Mountain View
                    .zoom(1)                   // Sets the zoom
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                float maxZoom = 4.0f;
                float minZoom = 2.0f;

                if (position.zoom > maxZoom) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(maxZoom));
                } else if (position.zoom < minZoom) {
                    map.animateCamera(CameraUpdateFactory.zoomTo(minZoom));
                }
            }
        });
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
    }
}
