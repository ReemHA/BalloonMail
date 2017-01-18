package com.balloonmail.app.balloonmailapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * Created by Dalia on 1/13/2017.
 */

public class LocationDialogPreference extends DialogPreference {


    public LocationDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(which == dialog.BUTTON_POSITIVE){

        }else{

        }
    }
}
