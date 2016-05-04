package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CustomSentExpandCard extends CardExpand implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context context;
    private FragmentManager fragmentManager;

    //Use your resource ID for your inner layout
    public CustomSentExpandCard(Context context) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
    }
    public CustomSentExpandCard(Context context, FragmentManager fragmentManager) {
        super(context, R.layout.card_sent_expand);
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve TextView elements
        TextView tx1 = (TextView) view.findViewById(R.id.e_textView);

        //Set value in text views
        if (tx1 != null) {
            tx1.setText("Expand done");
        }



        /*SupportMapFragment mapFragment =
                (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
    }
}
