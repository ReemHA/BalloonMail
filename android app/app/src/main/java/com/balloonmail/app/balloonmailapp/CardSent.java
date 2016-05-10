package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.Balloon;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CardSent extends Card {
    Balloon balloon;

    public CardSent(Balloon balloon, Context context) {
        super(context,R.layout.card_sent_item);
        this.balloon = balloon;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.sentBalloonTextTv);
            ImageButton mapBtn = (ImageButton) view.findViewById(R.id.mapImageButton);

            if (mTitleView != null && mapBtn != null){
                mTitleView.setText(balloon.getText());

                ViewToClickToExpand viewToClickToExpand =
                        ViewToClickToExpand.builder()
                                .setupView(mapBtn);
                setViewToClickToExpand(viewToClickToExpand);
            }

            TextView refill = (TextView)view.findViewById(R.id.refillTv);
            refill.setText(String.valueOf(balloon.getNoOfRefills()) + " refills");

            TextView reach = (TextView)view.findViewById(R.id.reachTv);
            reach.setText(String.valueOf(balloon.getReach()) + " reach");

            TextView creep = (TextView)view.findViewById(R.id.creepTv);
            creep.setText(String.valueOf(balloon.getNoOfCreeps()) + " creeps");
        }
    }
}