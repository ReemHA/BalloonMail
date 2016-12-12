package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.ActionButtonsHandler;
import com.balloonmail.app.balloonmailapp.utilities.MapsHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.gmariotti.cardslib.library.internal.Card;

import static com.balloonmail.app.balloonmailapp.utilities.Global.KEY_MAP_SAVED_STATE;

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

        MapsHandler.animateCameraToSource(map, sourceBalloon);

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
                Bundle mapState = (savedInstanceState != null)
                        ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE): null;
                mapView.onCreate(mapState); // Initialise the MapView
                mapView.getMapAsync(this); // Set the map ready callback to receive the GoogleMap object
                mapView.setClickable(false);
            }
        }

    }
}
