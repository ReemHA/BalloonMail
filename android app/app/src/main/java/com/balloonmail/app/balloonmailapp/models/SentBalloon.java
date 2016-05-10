package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */

@DatabaseTable(tableName="sentballoon")
public class SentBalloon extends Balloon {
    public SentBalloon() {
    }

    public SentBalloon(String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
    }
}
