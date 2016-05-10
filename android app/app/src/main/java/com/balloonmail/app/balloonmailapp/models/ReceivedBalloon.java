package com.balloonmail.app.balloonmailapp.models;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class ReceivedBalloon extends Balloon {

    public ReceivedBalloon() {
    }

    public ReceivedBalloon(String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
    }
}
