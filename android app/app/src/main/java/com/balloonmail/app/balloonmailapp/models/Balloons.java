package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Dalia on 4/23/2016.
 */
public abstract class Balloons {

    @DatabaseField(unique = true, generatedId = true)
    int balloonId;

    @DatabaseField
    String text;

    @DatabaseField
    int noOfRefills;

    @DatabaseField
    int noOfCreeps;

    @DatabaseField
    double distance;

    @DatabaseField
    double reach;

    @DatabaseField
    Date sentDate;

    public Balloons() {
    }

    public Balloons(String text){
        setText(text);
        this.sentDate = new Date(System.currentTimeMillis());
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getNoOfCreeps() {
        return noOfCreeps;
    }

    public void setNoOfCreeps(int noOfCreeps) {
        this.noOfCreeps = noOfCreeps;
    }

    public int getNoOfRefills() {
        return noOfRefills;
    }

    public void setNoOfRefills(int noOfRefills) {
        this.noOfRefills = noOfRefills;
    }

    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    public String getText() {
        return text;
    }

    private void setText(String text) {
        this.text = text;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public int getBalloonId() {
        return balloonId;
    }

    @Override
    public String toString() {
        return "Balloons{" +
                "balloonId=" + balloonId +
                ", text='" + text + '\'' +
                ", noOfRefills=" + noOfRefills +
                ", noOfCreeps=" + noOfCreeps +
                ", distance=" + distance +
                ", reach=" + reach +
                ", sentDate=" + sentDate +
                '}';
    }
}
