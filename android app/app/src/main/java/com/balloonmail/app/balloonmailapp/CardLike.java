package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.os.Bundle;

import com.balloonmail.app.balloonmailapp.models.LikedBalloon;

/**
 * Created by Reem Hamdy on 1/18/2017.
 */
public class CardLike extends CardReceived {
    public CardLike(LikedBalloon balloon, Context context, Bundle savedInstanceState) {
        super(balloon, context, savedInstanceState);
    }
}
