package com.balloonmail.app.balloonmailapp.async;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Reem Hamdy on 7/20/2016.
 */
public interface SuccessHandler<T> {
    T handle(JSONObject data) throws JSONException;


}
