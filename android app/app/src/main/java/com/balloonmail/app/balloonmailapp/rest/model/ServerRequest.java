package com.balloonmail.app.balloonmailapp.rest.model;

import java.io.Serializable;

/**
 * Created by Reem Hamdy on 5/2/2016.
 */
public class ServerRequest implements Serializable{
    final String user_name;
    final String access_token;

    public ServerRequest(String userName, String access_token) {
        this.user_name = userName;
        this.access_token = access_token;
    }
}
