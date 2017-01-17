package com.balloonmail.app.balloonmailapp.manager;

import android.content.Context;

import com.balloonmail.app.balloonmailapp.models.Balloon;

/**
 * Created by Reem Hamdy on 1/17/2017.
 */
public interface IActionUI {
    Context getCurrentContext();
    Balloon getBalloon();
}
