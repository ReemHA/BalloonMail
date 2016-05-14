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
public abstract class Balloon implements Serializable {

    @DatabaseField
    int balloon_id;

    @DatabaseField
    String text;

    @DatabaseField
    int refills;

    @DatabaseField
    int creeps;

    @DatabaseField
    double reach;

    @DatabaseField
    private double latSource;
    @DatabaseField
    private double lngSource;

    private HashMap<LatLng, ArrayList<LatLng>> destinationsHashMap;

    @DatabaseField
    double sentiment;

    @DatabaseField
    Date sent_at;

    public Balloon() {
    }

    public Balloon(String text, int noOfRefills, int noOfCreeps, double reach){
        this.text = text;
        this.sent_at = new Date(System.currentTimeMillis());
        this.refills = noOfRefills;
        this.creeps = noOfCreeps;
        this.reach = reach;

        // TODO will be replaced with the attributes of the balloon when received from the server
        this.latSource = 30.065136;
        this.lngSource = 31.278821;
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


    public double getReach() {
        return reach;
    }

    public void setReach(double reach) {
        this.reach = reach;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRefills(int refills) {
        this.refills = refills;
    }

    public void setBalloon_id(int balloon_id) {
        this.balloon_id = balloon_id;
    }

    public void setCreeps(int creeps) {
        this.creeps = creeps;
    }

    public void setSentiment(double sentiment) {
        this.sentiment = sentiment;
    }

    public void setSent_at(Date sent_at) {
        this.sent_at = sent_at;
    }

    public int getBalloon_id() {
        return balloon_id;
    }

    public double getSentiment() {
        return sentiment;
    }

    public int getCreeps() {
        return creeps;
    }

    public int getRefills() {
        return refills;
    }

    public Date getSent_at() {
        return sent_at;
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
                "balloon_id='" + balloon_id + '\'' +
                ", text='" + text + '\'' +
                ", refills=" + refills +
                ", creeps=" + creeps +
                ", reach=" + reach +
                ", sentiment=" + sentiment +
                ", sent_at=" + sent_at +
                '}';
    }
}
