package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.manager.AppManager;
import com.balloonmail.app.balloonmailapp.manager.ISentimentUI;
import com.balloonmail.app.balloonmailapp.manager.creep.ICreepableUI;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableUI;
import com.balloonmail.app.balloonmailapp.manager.refill.IRefillableUI;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
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
public class CardReceived extends Card implements ILikeableUI, IRefillableUI, ICreepableUI, ISentimentUI {
    private AppManager manager;
    ReceivedBalloon balloon;
    private Context context;
    private Bundle savedInstanceState;
    ReceivedCardViewHolder holder;

    public CardReceived(ReceivedBalloon balloon, Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_received_item);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;
        this.manager = AppManager.getInstance();
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

            /**
             * instantiate the actions buttons and sentiment indication states.
             */
            manager.instantiateLikeButtonState(getBalloon(), holder.likeBtn);
            manager.instantiateRefillButtonState(getBalloon(), holder.refillBtn);
            manager.instantiateCreepButtonState(getBalloon(), holder.creepBtn);
            manager.instantiateSentimentState(balloon.getSentiment(), holder.sentimentIndication);

            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.instantiateLikeButtonState(getBalloon(), holder.likeBtn);
                    manager.like(CardReceived.this);
                }
            });

            holder.refillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.instantiateRefillButtonState(getBalloon(), holder.refillBtn);
                    manager.refill(CardReceived.this);
                }
            });

            holder.creepBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manager.instantiateCreepButtonState(getBalloon(), holder.creepBtn);
                    manager.creep(CardReceived.this);
                }
            });

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


    @Override
    public ImageButton getCreepButton() {
        return holder.creepBtn;
    }

    @Override
    public ImageButton getLikeButton() {
        return holder.likeBtn;
    }

    @Override
    public ImageButton getRefillButton() {
        return holder.refillBtn;
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
        return holder.sentimentIndication;
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
                        ? savedInstanceState.getBundle(KEY_MAP_SAVED_STATE) : null;
                mapView.onCreate(mapState); // Initialise the MapView
                mapView.getMapAsync(this); // Set the map ready callback to receive the GoogleMap object
                mapView.setClickable(false);
            }
        }

    }
}
