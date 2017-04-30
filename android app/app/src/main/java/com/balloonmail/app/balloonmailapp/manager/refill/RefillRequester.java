package com.balloonmail.app.balloonmailapp.manager.refill;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.appinvite.AppInviteInvitation;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class RefillRequester {
    private final int REQUEST_INVITE = 2;
    public void requestRefillToServer(IRefillableUI refillableUI){
        final IRefillableModel refillableBalloon = (IRefillableModel) refillableUI.getBalloon();
        final ImageButton refillButton = refillableUI.getRefillButton();
        final RefillHandler refillHandler = new RefillHandler(refillableBalloon, refillButton);
        final Context currentContext = refillableUI.getCurrentContext();

        ReusableAsync<Void> task = new ReusableAsync<>(currentContext)
                .post("/balloons/refill")
                .bearer(Global.getApiToken(currentContext))
                .addData("balloon_id", Integer.toString(refillableUI.getBalloon().getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            // check if the balloon reached all users in the app
                            if (data.has("full")){
                                // show invite friends dialog
                                new AlertDialog.Builder(currentContext)
                                        .setTitle("Invite Friends")
                                        .setMessage("This balloon has reached all current BalloonMail users. " +
                                                "Spread BalloonMail message and " +
                                                "encourage your friends to spread positivity?")
                                        .setPositiveButton("Invite Friends", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                inviteFriends(currentContext);
                                            }
                                        })
                                        .setNegativeButton("Thanks", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        })
                                        .setIcon(R.drawable.logo_balloonmail_app)
                                        .show();
                            } else {
                                // get whether the balloon is refilled or not.
                                int isRefilled = refillableBalloon.getIsRefilled();
                                if (isRefilled == 0) {
                                    refillableBalloon.setIsRefilled(1);
                                } else {
                                    refillableBalloon.setIsRefilled(0);
                                }
                            }
                            return null;
                        }
                    })
                .onPost(new PostHandler() {
                    @Override
                    public void handle(Object data) {
                      refillHandler.handleButtonUI();
                    }
                });
                task.send();
    }

    private void inviteFriends(Context context){
        Intent intent = new AppInviteInvitation.IntentBuilder(
                (Resources.getSystem().getString(R.string.invitation_title)))
                .setMessage((Resources.getSystem().getString(R.string.invitation_message)))
                .setDeepLink(Uri.parse(((Resources.getSystem().getString(R.string.invitation_deep_link)))))
                .setCustomImage(Uri.parse(((Resources.getSystem().getString(R.string.invitation_custom_image)))))
                .setCallToActionText((Resources.getSystem().getString(R.string.invitation_cta)))
                .build();
        ((Activity) context).startActivityForResult(intent, REQUEST_INVITE);
    }
}
