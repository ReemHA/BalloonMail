package com.balloonmail.app.balloonmailapp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.balloonmail.app.balloonmailapp.R;
import com.google.android.gms.appinvite.AppInviteInvitation;

/**
 * Created by Reem Hamdy on 5/3/2016.
 */
public class InviteFriendsDialogPreference extends DialogPreference {
    private final int REQUEST_INVITE = 2;

    public InviteFriendsDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        Intent intent = new AppInviteInvitation.IntentBuilder(
                getContext().getResources().getString(R.string.invitation_title))
                .setMessage((getContext().getResources().getString(R.string.invitation_message)))
                .setDeepLink(Uri.parse((getContext().getResources().getString(R.string.invitation_deep_link))))
                .setCustomImage(Uri.parse((getContext().getResources().getString(R.string.invitation_custom_image))))
                .setCallToActionText(getContext().getResources().getString(R.string.invitation_cta))
                .build();
        ((Activity) getContext()).startActivityForResult(intent, REQUEST_INVITE);
    }
}
