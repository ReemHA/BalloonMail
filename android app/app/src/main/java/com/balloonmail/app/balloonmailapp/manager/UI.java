package com.balloonmail.app.balloonmailapp.manager;

import android.content.Context;

import com.balloonmail.app.balloonmailapp.models.Balloon;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public interface UI {
    Context getCurrentContext();
    Balloon getBalloon();
}
