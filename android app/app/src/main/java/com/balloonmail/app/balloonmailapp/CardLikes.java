package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;
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
public class CardLikes extends Card {


    Balloon balloon;
    private Context context;
    private Bundle savedInstanceState;

    LikedCardViewHolder holder;

    public CardLikes(Context context, Balloon balloon) {
        super(context,R.layout.card_likes_item);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.likedBalloonTextTv);

            if (mTitleView != null){
                mTitleView.setText(balloon.getText());

            }

            View row = view;

            if (row == null){ return; }

            holder = new LikedCardViewHolder();
            //holder.title = (TextView) view.findViewById(R.id.e_textView);
            holder.mapView = (MapView) view.findViewById(R.id.row_map);

            holder.initializeMapView();

            if (holder.map != null) {
                // The map is already ready to be used
                setMapLocation(holder.map);
            }

            holder.refillBtn = (ImageButton) view.findViewById(R.id.refillActionBtn_liked);
            holder.likeBtn = (ImageButton) view.findViewById(R.id.likeActionBtn_liked);
            holder.creepBtn = (ImageButton) view.findViewById(R.id.creepActionBtn_liked);

            //Initial States
            changeStateOfLikeBtn();
            changeStateOfRefillBtn();
            changeStateOfCreepBtn();

            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestLikeToServer();
                    changeStateOfLikeBtn();
                }
            });

            holder.refillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((LikedBalloon)balloon).setIs_refilled(1);
                    requestRefillToServer();
                    changeStateOfRefillBtn();
                }
            });

            holder.creepBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //((LikedBalloon)balloon).setIs_creeped(1);
                    requestCreepToServer();
                    changeStateOfCreepBtn();
                }
            });
        }
    }
    private void changeStateOfLikeBtn(){
        if(((LikedBalloon)balloon).getIs_liked() == 0){
            holder.likeBtn.setImageResource(R.drawable.ic_like_grey_24px);
        }else{
            holder.likeBtn.setImageResource(R.drawable.ic_like_clicked_24px);
        }
    }

    private void requestLikeToServer(){

    }

    private void changeStateOfRefillBtn(){
        if(((LikedBalloon)balloon).getIs_refilled() == 0){
            holder.refillBtn.setImageResource(R.drawable.ic_refill_grey_24px);
        }else{
            holder.refillBtn.setImageResource(R.drawable.ic_refill_primary_24px);
        }
    }

    private void requestRefillToServer(){

    }

    private void changeStateOfCreepBtn(){
        if(((LikedBalloon)balloon).getIs_creeped() == 0){
            holder.creepBtn.setImageResource(R.drawable.ic_creepy_grey_24px);
        }else{
            holder.creepBtn.setImageResource(R.drawable.ic_creepy_clicked_24px);
        }
    }

    private void requestCreepToServer(){

    }

    private void setMapLocation(GoogleMap map) {

        LatLng sourceBalloon = balloon.getSourceBalloon();

        if(sourceBalloon != null){
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

    class LikedCardViewHolder implements OnMapReadyCallback {

        MapView mapView;

        GoogleMap map;
        ImageButton refillBtn; ImageButton likeBtn; ImageButton creepBtn;

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
                //mapView.setClickable(false);

            }
        }

    }
}
