package com.balloonmail.app.balloonmailapp.manager.creep;

import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.manager.ActionHandler;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class CreepHandler extends ActionHandler {
    ICreepableModel creepableBalloon;
    ImageButton creepButton;

    public CreepHandler(ImageButton creepButton, ICreepableModel creepableBalloon) {
        this.creepButton = creepButton;
        this.creepableBalloon = creepableBalloon;
    }

    public CreepHandler(ICreepableModel creepableBalloon, ImageButton creepButton) {
        this.creepableBalloon = creepableBalloon;
        this.creepButton = creepButton;
    }

    @Override
    public void handleButtonUI() {
        if (creepableBalloon.getIsCreeped() == 0) {
            creepButton.setImageResource(R.drawable.ic_creepy_grey_24px);
        } else {
            creepButton.setImageResource(R.drawable.ic_creepy_clicked_24px);

        }
    }
    public boolean isCreeped() {
        if (creepableBalloon.getIsCreeped() == 1) {
            return true;
        }
        return false;
    }


}
