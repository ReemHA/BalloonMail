package com.balloonmail.app.balloonmailapp.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.manager.AppManager;
import com.balloonmail.app.balloonmailapp.manager.ISentimentUI;
import com.balloonmail.app.balloonmailapp.manager.creep.ICreepableUI;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableUI;
import com.balloonmail.app.balloonmailapp.manager.refill.IRefillableUI;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.utilities.MapsHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class ReceivedMailsDetailsFragment extends Fragment implements OnMapReadyCallback,
        ILikeableUI, IRefillableUI, ICreepableUI, ISentimentUI {
    private AppManager manager;
    private ImageButton refillBtn;
    private ImageButton creepBtn;
    private ImageButton likeBtn;
    private View sentimentView;
    ReceivedBalloon balloon;
    View rootView;
    GoogleMap map;

    public ReceivedMailsDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null)
            return rootView;

        rootView = inflater.inflate(R.layout.fragment_received_and_liked_mail_details, container, false);
        manager = AppManager.getInstance();
        balloon = (ReceivedBalloon) Global.balloonHolder.getBalloon();

        TextView text = (TextView) rootView.findViewById(R.id.sentBalloonTextTv);
        text.setText(balloon.getText());

        refillBtn = (ImageButton) rootView.findViewById(R.id.refillActionBtn_details);
        creepBtn = (ImageButton) rootView.findViewById(R.id.creepActionBtn_details);
        likeBtn = (ImageButton) rootView.findViewById(R.id.likeActionBtn_details);
        sentimentView = rootView.findViewById(R.id.sentiment_indication);

        manager.instantiateLikeButtonState(balloon, likeBtn);
        manager.instantiateRefillButtonState(balloon, refillBtn);
        manager.instantiateCreepButtonState(balloon, creepBtn);
        manager.instantiateSentimentState(balloon.getSentiment(), sentimentView);

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.like(ReceivedMailsDetailsFragment.this);
            }
        });
        refillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.refill(ReceivedMailsDetailsFragment.this);
            }
        });

        creepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.creep(ReceivedMailsDetailsFragment.this);
            }
        });


        //Map
        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_detail_fragment);
        mapFragment.getMapAsync(this);
        Global.balloonHolder.setBalloon(null);
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        LatLng sourceBalloon = balloon.getSourceBalloon();

        if (sourceBalloon != null) {
            MapsHandler.addSourceBalloonMarker(map, sourceBalloon);

            MapsHandler.animateCameraToSource(map, sourceBalloon);
        }


        MapsHandler.setMaximumZoomToMapAndDisableCompass(map, 7.0f, 2.0f);
    }

    @Override
    public ImageButton getCreepButton() {
        return creepBtn;
    }

    @Override
    public ImageButton getLikeButton() {
        return likeBtn;
    }

    @Override
    public ImageButton getRefillButton() {
        return refillBtn;
    }

    @Override
    public Context getCurrentContext() {
        return getContext();
    }

    @Override
    public ReceivedBalloon getBalloon() {
        return balloon;
    }

    @Override
    public View getSentimentBar() {
        return sentimentView;
    }
}
