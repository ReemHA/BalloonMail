package com.balloonmail.app.balloonmailapp.rest.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Reem Hamdy on 5/12/2016.
 */
public class SendBalloonRespond implements Serializable {
    String error;
    String balloon_id;
    String text;
    int refill;
    int creep;
    double reach;
    double sentiment;
    Date sent_date;

    public String getError() {
        return error;
    }

    public String getBalloon_id() {
        return balloon_id;
    }

    public String getText() {
        return text;
    }

    public int getRefill() {
        return refill;
    }

    public int getCreep() {
        return creep;
    }

    public double getReach() {
        return reach;
    }

    public double getSentiment() {
        return sentiment;
    }

    public Date getSent_date() {
        return sent_date;
    }

    public SendBalloonRespond(String text, String balloon_id, double reach, int creep, int refill, double sentiment, Date sent_date) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.reach = reach;
        this.creep = creep;
        this.refill = refill;
        this.sentiment = sentiment;
        this.sent_date = sent_date;
    }
}
