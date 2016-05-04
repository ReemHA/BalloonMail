package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.models.Balloon;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Dalia on 5/5/2016.
 */
public class CardLikes extends Card {

    Balloon balloon;

    public CardLikes(Context context, Balloon balloon) {
        super(context,R.layout.card_likes_item);
        this.balloon = balloon;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.likesBalloonTextTv);

            if (mTitleView != null){
                mTitleView.setText(balloon.getText());

            }
        }
    }
}
