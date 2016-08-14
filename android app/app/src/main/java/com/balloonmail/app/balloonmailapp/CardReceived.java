package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.ActionButtonsHandler;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Dalia on 5/5/2016.
 */
public class CardReceived extends Card {

    ReceivedBalloon balloon;
    private Context context;
    private Bundle savedInstanceState;
    ReceivedCardViewHolder holder;

    public CardReceived(ReceivedBalloon balloon, Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_received_item);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.receivedBalloonTextTv);

            if (mTitleView != null) {
                mTitleView.setText(balloon.getText());
            }

            View row = view;

            if (row == null) {
                return;
            }

            holder = new ReceivedCardViewHolder();
            holder.mapView = (MapView) view.findViewById(R.id.row_map);

            holder.initializeMapView();

            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map);
            }

            holder.refillBtn = (ImageButton) view.findViewById(R.id.refillActionBtn_received);
            holder.likeBtn = (ImageButton) view.findViewById(R.id.likeActionBtn_received);
            holder.creepBtn = (ImageButton) view.findViewById(R.id.creepActionBtn_received);
            holder.sentimentIndication = view.findViewById(R.id.sentiment_indication);

            ActionButtonsHandler.changeStateOfLikeBtn(balloon, holder.likeBtn);
            ActionButtonsHandler.changeStateOfRefillBtn(balloon, holder.refillBtn);
            ActionButtonsHandler.changeStateOfCreepBtn(balloon, holder.creepBtn);

            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionButtonsHandler.onClickOfLikeButton(balloon, getContext(), holder.likeBtn);
                }
            });

            holder.refillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionButtonsHandler.onClickOfRefillButton(balloon, getContext(), holder.refillBtn);
                }
            });

            holder.creepBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActionButtonsHandler.onClickOfCreepButton(balloon, getContext(), holder.creepBtn);
                }
            });

            ActionButtonsHandler.changeColorOfSentimentIndication(balloon.getSentiment(), holder.sentimentIndication);
        }
    }

    private void setMapLocation(GoogleMap map) {

        LatLng sourceBalloon = balloon.getSourceBalloon();

        if (sourceBalloon != null) {
            map.addMarker(new MarkerOptions()
                    .position(sourceBalloon)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_source_balloon)));
        }

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(sourceBalloon)      // Sets the center of the map to Mountain View
                .zoom(1)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    class ReceivedCardViewHolder implements OnMapReadyCallback {

        MapView mapView;

        GoogleMap map;

        ImageButton refillBtn;
        ImageButton likeBtn;
        ImageButton creepBtn;
        View sentimentIndication;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation(map);
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
