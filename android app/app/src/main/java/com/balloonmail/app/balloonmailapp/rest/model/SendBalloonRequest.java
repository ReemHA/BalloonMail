package com.balloonmail.app.balloonmailapp.rest.model;

import java.io.Serializable;

/**
 * Created by Reem Hamdy on 5/5/2016.
 */
public class SendBalloonRequest implements Serializable {
    final String text;

    public SendBalloonRequest(String text) {
        this.text = text;
    }
}
