package com.balloonmail.app.balloonmailapp.manager;

import android.view.View;

import com.balloonmail.app.balloonmailapp.R;

/**
 * Created by Reem Hamdy on 1/17/2017.
 */
public abstract class ActionHandler {
    public abstract void handleButtonUI();
    public static void changeColorOfSentimentIndication(double sentiment, View view){
        if(sentiment < 0){
            view.setBackgroundResource(R.color.red);
        }else if(sentiment > 0){
            view.setBackgroundResource(R.color.green);
        }else{
            view.setBackgroundResource(R.color.colorPrimary);
        }
    }
}
