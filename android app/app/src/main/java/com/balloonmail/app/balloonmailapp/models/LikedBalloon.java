package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class LikedBalloon extends Balloon {


    @DatabaseField
    int is_refilled;

    @DatabaseField
    int is_liked;

    @DatabaseField
    int is_creeped;

    public LikedBalloon(){

    }
    public LikedBalloon (String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
        is_refilled = 0; is_liked = 1; is_creeped = 0;
    }
}
