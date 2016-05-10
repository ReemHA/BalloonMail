package com.balloonmail.app.balloonmailapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Dalia on 4/23/2016.
 */
public abstract class Balloon implements Serializable{

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
    private double latSource;
    @DatabaseField
    private double lngSource;

    private HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap;

    @DatabaseField
    Date sentDate;

    public Balloon() {
    }

    public Balloon(String text, int noOfRefills, int noOfCreeps, double reach){
        this.text = text;
        this.sentDate = new Date(System.currentTimeMillis());
        this.noOfRefills = noOfRefills;
        this.noOfCreeps = noOfCreeps;
        this.reach = reach;

        // TODO will be replaced with the attributes of the balloon when received from the server
        this.latSource = 30.065136;
        this.lngSource = 31.278821;
        initializeHashMap();
    }

    private void initializeHashMap(){
        this.destinationsHashMap = new HashMap<>();
        ArrayList<LatLng> destinationsArrayList = new ArrayList<>();
        destinationsArrayList.add(new LatLng(-37.81319, 144.96298));
        destinationsArrayList.add(new LatLng(-33.87365, 151.20689));
        destinationsArrayList.add(new LatLng(-34.92873, 138.59995));
        destinationsArrayList.add(new LatLng(-31.95285, 115.85734));
        destinationsArrayList.add(new LatLng(51.471547, -0.460052));
        destinationsArrayList.add(new LatLng(33.936524, -118.377686));
        destinationsArrayList.add(new LatLng(40.641051, -73.777485));
        destinationsArrayList.add(new LatLng(-37.006254, 174.783018));
        this.destinationsHashMap.put(new LatLng(latSource, lngSource), destinationsArrayList);
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

    public HashMap<LatLng, ArrayList<LatLng>> getDestinationsHashMap() {
        return destinationsHashMap;
    }

    public void setDestinationsHashMap(HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap) {
        this.destinationsHashMap = destinationsHashMap;
    }

    public LatLng getSourceBalloon() {
        return new LatLng(latSource, lngSource);
    }

    public void setSourceBalloon(double lat, double lng) {
        this.latSource = lat;
        this.lngSource = lng;
    }

    @Override
    public String toString() {
        return "Balloon{" +
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
