package com.balloonmail.app.balloonmailapp.rest;

import com.balloonmail.app.balloonmailapp.rest.model.LoginServerRequest;
import com.balloonmail.app.balloonmailapp.rest.model.LoginServerResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Reem Hamdy on 5/2/2016.
 */
public interface RInterface {

    @Headers({"Content-type: application/json"})
    @POST("/token/google")
    Call<LoginServerResponse> postData(@Body LoginServerRequest body);

}
