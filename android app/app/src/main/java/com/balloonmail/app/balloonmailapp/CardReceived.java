package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Dalia on 5/5/2016.
 */
public class CardReceived extends Card {

    Balloon balloon;
    private Context context;
    private Bundle savedInstanceState;
    private SharedPreferences sharedPreferences;
    private static String api_token;
    ReceivedCardViewHolder holder;

    public CardReceived(Balloon balloon, Context context, Bundle savedInstanceState) {
        super(context, R.layout.card_received_item);
        this.balloon = balloon;
        this.context = context;
        this.savedInstanceState = savedInstanceState;
        sharedPreferences = context.getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);

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

            api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");
            holder.refillBtn = (ImageButton) view.findViewById(R.id.refillActionBtn_received);
            holder.likeBtn = (ImageButton) view.findViewById(R.id.likeActionBtn_received);
            holder.creepBtn = (ImageButton) view.findViewById(R.id.creepActionBtn_received);
            holder.sentimentIndication = view.findViewById(R.id.sentiment_indication);

            changeStateOfLikeBtn();
            changeStateOfRefillBtn();
            changeStateOfCreepBtn();

            holder.likeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestLikeToServer(balloon);
                }
            });

            holder.refillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ReceivedBalloon) balloon).getIs_refilled() == 0) {
                        requestRefillToServer(balloon);
                    } else {
                        // in case no internet connection the server conn fail msg should appear
                        if (!Global.isConnected(context)) {
                            Global.showMessage(context, "No internet connection",
                                    Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
                        } else {
                            Global.showMessage(context, "refill btn clicked twice",
                                    Global.ERROR_MSG.REFILL_REQ_FAIL.getMsg());
                        }
                    }
                }
            });

            holder.creepBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((ReceivedBalloon) balloon).getIs_creeped() == 0) {
                        requestCreepToServer(balloon);
                    } else {

                        // in case no internet connection the server conn fail msg should appear
                        if (!Global.isConnected(context)) {
                            Global.showMessage(context, "No internet connection",
                                    Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
                        } else {
                            Global.showMessage(context, "creep btn clicked twice",
                                    Global.ERROR_MSG.CREEP_REQ_FAIL.getMsg());
                        }
                    }
                }
            });

            changeColorOfSentimentIndication(balloon.getSentiment());
        }
    }

    private void changeStateOfLikeBtn() {
        if (((ReceivedBalloon) balloon).getIs_liked() == 0) {
            holder.likeBtn.setImageResource(R.drawable.ic_like_grey_24px);
        } else {
            holder.likeBtn.setImageResource(R.drawable.ic_like_clicked_24px);
        }
    }

    private void requestLikeToServer(Balloon likedBalloon) {
        new ReusableAsync<Void>(context)
                .bearer(api_token)
                .post("/balloons/like")
                .addData("balloon_id", Integer.toString(likedBalloon.getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject data) {
                        int isLiked = ((ReceivedBalloon) balloon).getIs_liked();
                        if (isLiked == 0) {
                            ((ReceivedBalloon) balloon).setIs_liked(1);
                        } else {
                            ((ReceivedBalloon) balloon).setIs_liked(0);
                        }
                        return null;
                    }
                })
                .onPost(new PostHandler<Void>() {
                    @Override
                    public void handle(Void data) {
                        changeStateOfLikeBtn();
                    }
                })
                .send();
    }

    private void requestRefillToServer(Balloon refilledBalloon) {
        new ReusableAsync<Void>(context)
                .post("/balloons/refill")
                .bearer(Global.getApiToken(getContext()))
                .addData("balloon_id", Integer.toString(refilledBalloon.getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject data) throws JSONException {
                        int isRefilled = ((ReceivedBalloon) balloon).getIs_refilled();
                        if (isRefilled == 0) {
                            ((ReceivedBalloon) balloon).setIs_refilled(1);
                        } else {
                            ((ReceivedBalloon) balloon).setIs_refilled(0);
                        }
                        return null;
                    }
                })
                .onPost(new PostHandler<Void>() {
                    @Override
                    public void handle(Void data) {
                        changeStateOfRefillBtn();
                    }
                })
                .send();
    }

    private void requestCreepToServer(Balloon creepedBalloon) {
        new ReusableAsync<Void>(context)
                .post("/balloons/creep")
                .bearer(Global.getApiToken(getContext()))
                .addData("balloon_id", Integer.toString(creepedBalloon.getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject data) throws JSONException {
                        int isCreeped = ((ReceivedBalloon) balloon).getIs_creeped();
                        if (isCreeped == 0) {
                            ((ReceivedBalloon) balloon).setIs_creeped(1);
                        } else {
                            ((ReceivedBalloon) balloon).setIs_creeped(0);
                        }
                        return null;
                    }
                })
                .onPost(new PostHandler<Void>() {
                    @Override
                    public void handle(Void data) {
                        changeStateOfCreepBtn();
                    }
                })
                .send();
    }

    private void changeStateOfRefillBtn() {
        if (((ReceivedBalloon) balloon).getIs_refilled() == 0) {
            holder.refillBtn.setImageResource(R.drawable.ic_refill_grey_24px);
        } else {
            holder.refillBtn.setImageResource(R.drawable.ic_refill_primary_24px);
        }
    }

    private void changeStateOfCreepBtn() {
        if (((ReceivedBalloon) balloon).getIs_creeped() == 0) {
            holder.creepBtn.setImageResource(R.drawable.ic_creepy_grey_24px);
        } else {
            holder.creepBtn.setImageResource(R.drawable.ic_creepy_clicked_24px);
        }
    }

    private void changeColorOfSentimentIndication(double sentiment) {
        if (sentiment < 0) {
            holder.sentimentIndication.setBackgroundResource(R.color.red);
        } else if (sentiment > 0) {
            holder.sentimentIndication.setBackgroundResource(R.color.green);
        } else {
            holder.sentimentIndication.setBackgroundResource(R.color.colorPrimary);
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
