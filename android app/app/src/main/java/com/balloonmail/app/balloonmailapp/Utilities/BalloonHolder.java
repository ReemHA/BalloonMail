package com.balloonmail.app.balloonmailapp.Utilities;

import com.balloonmail.app.balloonmailapp.models.SentBalloon;

/**
 * Created by Reem Hamdy on 5/10/2016.
 */
public class BalloonHolder {
    private SentBalloon balloon;
    private static BalloonHolder holder = new BalloonHolder();

    public static BalloonHolder getInstance() {
        return holder;
    }

    public void setBalloon(SentBalloon balloon) {
        this.balloon = balloon;
    }

    public SentBalloon getBalloon() {
        return balloon;
    }
}
