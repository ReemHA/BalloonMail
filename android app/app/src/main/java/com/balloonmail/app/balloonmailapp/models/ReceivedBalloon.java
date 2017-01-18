package com.balloonmail.app.balloonmailapp.models;

import com.balloonmail.app.balloonmailapp.manager.creep.ICreepableModel;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableModel;
import com.balloonmail.app.balloonmailapp.manager.refill.IRefillableModel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */

@DatabaseTable(tableName="receivedballoon")
public class ReceivedBalloon extends Balloon implements ILikeableModel, IRefillableModel, ICreepableModel {

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

    public ReceivedBalloon(String text, int balloon_id,double sentiment, double lat, double lng,Date sent_date) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.sentiment = sentiment;
        this.setSourceBalloon(lat, lng);
        this.sent_at = sent_date;
    }
    @Override
    public void setIsCreeped(int x) {
        this.is_creeped = x;
    }

    @Override
    public int getIsCreeped() {
        return is_creeped;
    }

    @Override
    public void setIsLiked(int x) {
        this.is_liked = x;
    }

    @Override
    public int getIsLiked() {
        return is_liked;
    }

    @Override
    public void setIsRefilled(int x) {
        this.is_refilled = x;
    }

    @Override
    public int getIsRefilled() {
        return is_refilled;
    }
}
