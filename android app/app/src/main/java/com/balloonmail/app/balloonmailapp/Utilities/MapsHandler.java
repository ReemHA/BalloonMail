package com.balloonmail.app.balloonmailapp.utilities;

import android.graphics.Color;

import com.balloonmail.app.balloonmailapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dalia on 9/17/2016.
 */
public class MapsHandler {

    public static void setMaximumZoomToMapAndDisableCompass(final GoogleMap map, final float max, final float min){
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                float maxZoom = max;
                float minZoom = min;

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

    public static void addSourceBalloonMarker(final GoogleMap map, LatLng sourceBalloon){
        map.addMarker(new MarkerOptions()
                .position(sourceBalloon)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_source_balloon)));
    }

    public static void addDestinationsBalloonPolylines(final GoogleMap map, LatLng sourceBalloon, HashMap<LatLng,
            ArrayList<LatLng>> destinationsHashMap, boolean ifAddRefillPolylines){

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
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destination)));

            //check if destination has another refills
            if(ifAddRefillPolylines == true){
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
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_destination_light)));
                    }
                }
            }
        }
    }

    public static void animateCameraToSource(final GoogleMap map, LatLng sourceBalloon){
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sourceBalloon)      // Sets the center of the map to source balloon
                .zoom(1)                   // Sets the zoom
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
