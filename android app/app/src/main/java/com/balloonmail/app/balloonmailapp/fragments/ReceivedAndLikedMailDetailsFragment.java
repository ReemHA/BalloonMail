package com.balloonmail.app.balloonmailapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.utilities.ActionButtonsHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.utilities.MapsHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ReceivedAndLikedMailDetailsFragment extends Fragment implements OnMapReadyCallback{

    Balloon balloon;
    View rootView;
    GoogleMap map;
    ImageButton refillBtn;
    ImageButton creepBtn;
    ImageButton likeBtn;

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

        refillBtn = (ImageButton)rootView.findViewById(R.id.refillActionBtn_details);
        creepBtn = (ImageButton)rootView.findViewById(R.id.creepActionBtn_details);
        likeBtn = (ImageButton)rootView.findViewById(R.id.likeActionBtn_details);


        ActionButtonsHandler.changeStateOfLikeBtn(balloon, likeBtn);
        ActionButtonsHandler.changeStateOfRefillBtn(balloon, refillBtn);
        ActionButtonsHandler.changeStateOfCreepBtn(balloon, creepBtn);

        refillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionButtonsHandler.onClickOfRefillButton(balloon, getContext(), refillBtn);
            }
        });

        creepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionButtonsHandler.onClickOfCreepButton(balloon, getContext(), creepBtn);
            }
        });

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActionButtonsHandler.onClickOfLikeButton(balloon, getContext(), likeBtn);
            }
        });

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
            MapsHandler.addSourceBalloonMarker(map, sourceBalloon);

            MapsHandler.animateCameraToSource(map, sourceBalloon);
        }


        MapsHandler.setMaximumZoomToMapAndDisableCompass(map, 7.0f, 2.0f);
    }
}
