package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.field.DatabaseField;

import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
@DatabaseTable(tableName="likedballoon")
public class LikedBalloon extends ReceivedBalloon {


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
        is_liked = 1;
    }

    public LikedBalloon(String text, int balloon_id, double sentiment, double lat, double lng, Date sent_date) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.sentiment = sentiment;
        this.setSourceBalloon(lat, lng);
        this.sent_at = sent_date;
        is_liked = 1;
    }

    @Override
    public void setIsLiked(int x) {
        super.setIsLiked(is_liked);
    }
}
