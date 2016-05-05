package com.balloonmail.app.balloonmailapp.rest.model;

import java.io.Serializable;

/**
 * Created by Reem Hamdy on 5/2/2016.
 */
public class ServerResponse implements Serializable{
    private String api_token;
    private boolean created;
    private String error;

    ServerResponse(String apiToken, boolean created) {
        this.api_token = apiToken;
        this.created = created;
    }

    public String getApi_token() {
        return api_token;
    }

    public boolean isCreated() {
        return created;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "ServerResponse{" +
                "created=" + created +
                ", api_token='" + api_token + '\'' +
                '}';
    }
}
