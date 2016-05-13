package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;

import com.balloonmail.app.balloonmailapp.Utilities.Global;

/**
 * Created by Reem Hamdy on 5/3/2016.
 */
public class SignOutDialogPreference extends DialogPreference {

    public SignOutDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == dialog.BUTTON_POSITIVE){
            Intent intent = new Intent(getContext(), LoginTabbedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("isSignedOut", true);
            Log.d(Global.LOG_TAG, SignOutDialogPreference.class.getSimpleName());
            getContext().startActivity(intent);
        }else{

        }
    }
}
