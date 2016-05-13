package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */

/**
 * This class acts as the response that returns back from the server based on SendBalloonRequest
 */
@DatabaseTable(tableName="sentballoon")
public class SentBalloon extends Balloon {

    public SentBalloon() {
    }

    public SentBalloon(String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
    }

    public SentBalloon(String text, int balloon_id, double reach, int creep, int refill, double sentiment, Date sent_date) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.reach = reach;
        this.creeps = creep;
        this.refills = refill;
        this.sentiment = sentiment;
        this.sent_at = sent_date;
    }
}
