package com.balloonmail.app.balloonmailapp.manager.refill;

import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.manager.ActionHandler;

/**
 * Created by Reem Hamdy on 1/14/2017.
 * This class handles any UI related issues of the refill button.
 */
public class RefillHandler extends ActionHandler{
    private IRefillableModel refillableBalloon;
    private ImageButton refillButton;

    public RefillHandler(IRefillableModel refillableBalloon, ImageButton refillButton) {
        this.refillableBalloon = refillableBalloon;
        this.refillButton = refillButton;
    }

    @Override
    public void handleButtonUI() {
        if (refillableBalloon.getIsRefilled() == 0){
            refillButton.setImageResource(R.drawable.ic_refill_grey_24px);
        }else {
            refillButton.setImageResource(R.drawable.ic_refill_primary_24px);
        }
    }

    public boolean isRefilled() {
        if (refillableBalloon.getIsRefilled() == 1) {
            return true;
        }
        return false;
    }
}