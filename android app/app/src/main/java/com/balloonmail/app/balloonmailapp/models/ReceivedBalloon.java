package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.table.DatabaseTable;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */

@DatabaseTable(tableName="receivedballoon")
public class ReceivedBalloon extends Balloon {

    @DatabaseField
    int is_refilled;

    @DatabaseField
    int is_liked;

    @DatabaseField
    int is_creeped;

    public ReceivedBalloon() {
    }

    public ReceivedBalloon(String text, int noOfRefills, int noOfCreeps, double reach) {
        super(text, noOfRefills, noOfCreeps, reach);
    }

    public int getIs_creeped() {
        return is_creeped;
    }

    public void setIs_creeped(int is_creeped) {
        this.is_creeped = is_creeped;
    }

    public int getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(int is_liked) {
        this.is_liked = is_liked;
    }

    public int getIs_refilled() {
        return is_refilled;
    }

    public void setIs_refilled(int is_refilled) {
        this.is_refilled = is_refilled;
    }

    public ReceivedBalloon(String text, int balloon_id,double sentiment, double lat, double lng,Date sent_date) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.sentiment = sentiment;
        this.setSourceBalloon(lat, lng);
        this.sent_at = sent_date;
    }
    public void onLikeClick(){
        if (is_liked == 0) {
            setIs_liked(1);
        } else {
            setIs_liked(0);
        }
    }
    public void onRefillClick(){
        if (is_refilled == 0) {
            setIs_refilled(1);
        } else {
            setIs_refilled(0);
        }
    }
    public void onCreepClick(){
        if (is_creeped == 0) {
            setIs_creeped(1);
        } else {
            setIs_creeped(0);
        }
    }
}
