package com.balloonmail.app.balloonmailapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.utilities.ActionButtonsHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ReceivedAndLikedMailDetailsFragment extends Fragment implements OnMapReadyCallback{

    Balloon balloon;
    View rootView;
    GoogleMap map;

    public ReceivedAndLikedMailDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null)
            return rootView;

        rootView = inflater.inflate(R.layout.fragment_received_and_liked_mail_details, container, false);

        balloon = Global.balloonHolder.getBalloon();

        TextView text = (TextView)rootView.findViewById(R.id.sentBalloonTextTv);
        text.setText(balloon.getText());

        //Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_detail_fragment);

        mapFragment.getMapAsync(this);

        View sentimentView = rootView.findViewById(R.id.sentiment_indication);
        ActionButtonsHandler.changeColorOfSentimentIndication(balloon.getSentiment(), sentimentView);


        Global.balloonHolder.setBalloon(null);
        
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sourceBalloon = balloon.getSourceBalloon();

        if(sourceBalloon != null){
            map.addMarker(new MarkerOptions()
                    .position(sourceBalloon)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_source_balloon)));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(sourceBalloon)      // Sets the center of the map to Mountain View
                    .zoom(1)                   // Sets the zoom
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }


        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
    }
}
