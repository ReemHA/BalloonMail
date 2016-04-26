package com.balloonmail.app.balloonmail.models;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class User {

    @DatabaseField
    String user_name;

    @DatabaseField(unique = true)
    String api_token;

    @DatabaseField
    Date login_date;

    public User() {
    }

    public User(String api_token, String user_name) {
        this.api_token = api_token;
        this.user_name = user_name;
        this.login_date = new Date(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "User{" +
                "user_name='" + user_name + '\'' +
                ", api_token='" + api_token + '\'' +
                ", login_date=" + login_date +
                '}';
    }
}
