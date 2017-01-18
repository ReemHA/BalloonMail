package com.balloonmail.app.balloonmailapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.manager.AppManager;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.utilities.MapsHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class SentMailDetailsFragment extends Fragment implements OnMapReadyCallback {
    private AppManager manager;
    Balloon balloon;
    View rootView;
    GoogleMap map;

    public SentMailDetailsFragment() {
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

        rootView = inflater.inflate(R.layout.fragment_sent_mail_details, container, false);
        manager = AppManager.getInstance();
        balloon = Global.balloonHolder.getBalloon();

        TextView text = (TextView) rootView.findViewById(R.id.sentBalloonTextTv);
        text.setText(balloon.getText());

        TextView refill = (TextView) rootView.findViewById(R.id.refillTv_details);
        refill.setText(String.valueOf(balloon.getRefills()) + " refills");


        TextView creep = (TextView) rootView.findViewById(R.id.creepTv_details);
        creep.setText(String.valueOf(balloon.getCreeps()) + " creeps");

        //Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_detail_fragment);

        mapFragment.getMapAsync(this);

        View sentimentView = rootView.findViewById(R.id.sentiment_indication);
        /**
         * call manager to instantiate sentiment state.
         */
        manager.instantiateSentimentState(balloon.getSentiment(), sentimentView);
        Global.balloonHolder.setBalloon(null);


        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setMapLocation(map);
    }

    private void setMapLocation(GoogleMap map) {
        LatLng sourceBalloon = balloon.getSourceBalloon();
        HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap = balloon.getDestinationsHashMap();


        if (sourceBalloon != null) {
            MapsHandler.addSourceBalloonMarker(map, sourceBalloon);

            if (destinationsHashMap != null) {
                MapsHandler.addDestinationsBalloonPolylines(map, sourceBalloon, destinationsHashMap, true);
            }

            MapsHandler.animateCameraToSource(map, sourceBalloon);
        }

        MapsHandler.setMaximumZoomToMapAndDisableCompass(map, 7.0f, 2.0f);
    }

}
