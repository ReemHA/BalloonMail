package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmailapp.activities.SentMailDetailsActivity;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.utilities.ActionButtonsHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.utilities.MapsHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CardSentExpand extends CardExpand{

    Balloon balloon;

    private Context context;
    private Bundle savedInstanceState;

    SentExpandCardViewHolder holder;

    //Use your resource ID for your inner layout
    public CardSentExpand(Context context) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
    }
    public CardSentExpand(Balloon balloon, Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_sent_expand);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;
    }


    public void setPathsOnMap(Balloon balloon){
        this.balloon = balloon;
        createMapOfExpandCard();
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view){
        View row = view;

        if (row == null){ return; }

        holder = new SentExpandCardViewHolder();

        holder.mapView = (MapView) view.findViewById(R.id.row_map);

        createMapOfExpandCard();

        holder.sentimentIndication = view.findViewById(R.id.sentiment_indication);

        ActionButtonsHandler.changeColorOfSentimentIndication(balloon.getSentiment(), holder.sentimentIndication);
    }

    public void createMapOfExpandCard(){
        holder.initializeMapView();

        if (holder.map != null) {
            // The map is already ready to be used
            setMapLocation(holder.map);
        }
    }

    private void setMapLocation(GoogleMap map) {

        LatLng sourceBalloon = balloon.getSourceBalloon();
        HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap = balloon.getDestinationsHashMap();

        if(sourceBalloon != null){
            MapsHandler.addSourceBalloonMarker(map, sourceBalloon);

            if(destinationsHashMap != null){
                MapsHandler.addDestinationsBalloonPolylines(map, sourceBalloon, destinationsHashMap, false);
            }

            MapsHandler.animateCameraToSource(map, sourceBalloon);
        }


        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Intent intent = new Intent(context, SentMailDetailsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("balloon", balloon);
                //intent.putExtras(bundle);
                Global.balloonHolder.setBalloon((SentBalloon) balloon);

                context.startActivity(intent);
            }
        });
    }

    class SentExpandCardViewHolder implements OnMapReadyCallback {

        MapView mapView;

        GoogleMap map;
        View sentimentIndication;

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            /*NamedLocation data = (NamedLocation) mapView.getTag();
            if (data != null) {
                setMapLocation(map, data);
            }*/
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
                //mapView.setClickable(false);

            }
        }

    }


    public Balloon getBalloon() {
        return balloon;
    }

    public void setBalloon(Balloon balloon) {
        this.balloon = balloon;
    }

}
