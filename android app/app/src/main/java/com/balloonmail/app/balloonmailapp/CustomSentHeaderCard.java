package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.CardHeader;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CustomSentHeaderCard extends CardHeader {
    //Use your resource ID for your inner layout
    public CustomSentHeaderCard(Context context) {
        super(context, R.layout.card_sent_header);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve TextView elements
        TextView tx1 = (TextView) view.findViewById(R.id.header_textView);
        TextView tx2 = (TextView) view.findViewById(R.id.header2_textView);

        //Set value in text views
        if (tx1 != null) {
            tx1.setText("This is an example of a text message of a very looong one to test");
        }
        if(tx2 != null){
            tx2.setText("2 This is an example of a text message of a very looong one to test");
        }
    }
}
