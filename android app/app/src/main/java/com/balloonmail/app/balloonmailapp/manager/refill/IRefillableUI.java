package com.balloonmail.app.balloonmailapp.manager.refill;

import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.manager.IActionUI;

/**
 * Created by Reem Hamdy on 1/13/2017.
 */
public interface IRefillableUI extends IActionUI {
    ImageButton getRefillButton();
}
