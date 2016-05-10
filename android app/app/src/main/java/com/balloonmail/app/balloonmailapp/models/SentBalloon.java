package com.balloonmail.app.balloonmailapp.models;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class SentBalloon extends Balloon {
    public SentBalloon() {
    }

    public SentBalloon(String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
    }
}
