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
    int refills;
    int creeps;
    double reach;
    double sentiment;
    Date sent_at;

    public String getError() {
        return error;
    }

    public String getBalloon_id() {
        return balloon_id;
    }

    public String getText() {
        return text;
    }

    public int getRefills() {
        return refills;
    }

    public int getCreeps() {
        return creeps;
    }

    public double getReach() {
        return reach;
    }

    public double getSentiment() {
        return sentiment;
    }

    public Date getSent_at() {
        return sent_at;
    }

    public SendBalloonRespond(String text, String balloon_id, double reach, int creeps, int refills, double sentiment, Date sent_at) {
        this.text = text;
        this.balloon_id = balloon_id;
        this.reach = reach;
        this.creeps = creeps;
        this.refills = refills;
        this.sentiment = sentiment;
        this.sent_at = sent_at;
    }

    @Override
    public String toString() {
        return "SendBalloonRespond{" +
                "error='" + error + '\'' +
                ", balloon_id='" + balloon_id + '\'' +
                ", text='" + text + '\'' +
                ", refills=" + refills +
                ", creeps=" + creeps +
                ", reach=" + reach +
                ", sentiment=" + sentiment +
                ", sent_at=" + sent_at +
                '}';
    }
}
