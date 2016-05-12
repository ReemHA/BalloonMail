package com.balloonmail.app.balloonmailapp.rest;

import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.rest.model.LoginServerRequest;
import com.balloonmail.app.balloonmailapp.rest.model.LoginServerResponse;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRequest;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Reem Hamdy on 5/2/2016.
 */
public interface RInterface {

    @POST("/token/google")
    Call<LoginServerResponse> postData(@Body LoginServerRequest body);

    @POST("/balloons/create")
    Call<SentBalloon> postSentBalloon(@Body SendBalloonRequest body);

    @GET("balloons/sent?{limit}?{last_date}")
    Call<List<SentBalloon>> requestSentBalloonListWithDate(@Path("limit") int limit, @Path("last_date") Date date);

    @GET("balloons/sent?{limit}")
    Call<List<SentBalloon>> requestSentBalloonList(@Path("limit") int limit);

}
