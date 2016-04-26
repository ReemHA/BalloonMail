package com.balloonmail.app.balloonmail.models;

/**
 * Created by Dalia on 4/23/2016.
 */
public abstract class Balloons {
    String text;
    int noOfRefills;
    int noOfCreeps;
    double distance;
    double reach;
    double sentDate;

    public Balloons(String text){
        this.text = text;
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

    public double getSentDate() {
        return sentDate;
    }

    public void setSentDate(double sentDate) {
        this.sentDate = sentDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
