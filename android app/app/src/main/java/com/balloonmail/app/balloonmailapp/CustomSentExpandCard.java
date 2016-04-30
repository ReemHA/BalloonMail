package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CustomSentExpandCard extends CardExpand {

    //Use your resource ID for your inner layout
    public CustomSentExpandCard(Context context) {
        super(context, R.layout.card_sent_expand);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve TextView elements
        TextView tx1 = (TextView) view.findViewById(R.id.e_textView);
        TextView tx2 = (TextView) view.findViewById(R.id.e2_textView);

        //Set value in text views
        if (tx1 != null) {
            tx1.setText("Expand done");
        }
    }
}
