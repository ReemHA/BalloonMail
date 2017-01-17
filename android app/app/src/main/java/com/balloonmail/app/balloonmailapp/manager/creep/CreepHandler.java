package com.balloonmail.app.balloonmailapp.manager.creep;

import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class CreepHandler{

    public static void handleButtonUI(ICreepableModel creepableBalloon, ImageButton creepButton) {
        if (creepableBalloon.getIsCreeped() == 0){
            creepButton.setImageResource(R.drawable.ic_creepy_grey_24px);
        }else{
            creepButton.setImageResource(R.drawable.ic_creepy_clicked_24px);

        }
    }
}
