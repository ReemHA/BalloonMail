package com.balloonmail.app.balloonmailapp.utilities;

import com.balloonmail.app.balloonmailapp.models.Balloon;

/**
 * Created by Reem Hamdy on 5/10/2016.
 */
public class BalloonHolder {
    private Balloon balloon;
    private static BalloonHolder holder = new BalloonHolder();

    public static BalloonHolder getInstance() {
        return holder;
    }

    public void setBalloon(Balloon balloon) {
        this.balloon = balloon;
    }

    public Balloon getBalloon() {
        return balloon;
    }
}
