package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.field.DatabaseField;

import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
@DatabaseTable(tableName="likedballoon")
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
}
