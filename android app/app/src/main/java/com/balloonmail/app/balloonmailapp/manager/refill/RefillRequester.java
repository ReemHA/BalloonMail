package com.balloonmail.app.balloonmailapp.manager.refill;

import android.content.Context;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class RefillRequester {
    public void requestRefillToServer(IRefillableUI refillableUI){
        final IRefillableModel refillableBalloon = (IRefillableModel) refillableUI.getBalloon();
        final ImageButton refillButton = refillableUI.getRefillButton();
        final RefillHandler refillHandler = new RefillHandler(refillableBalloon, refillButton);
        Context currentContext = refillableUI.getCurrentContext();

        ReusableAsync<Void> task = new ReusableAsync<>(currentContext)
                .post("/balloons/refill")
                .bearer(Global.getApiToken(currentContext))
                .addData("balloon_id", Integer.toString(refillableUI.getBalloon().getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            // get whether the balloon is refilled or not.
                            int isRefilled = refillableBalloon.getIsRefilled();
                            if (isRefilled == 0){
                                refillableBalloon.setIsRefilled(1);
                            }else {
                                refillableBalloon.setIsRefilled(0);
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
}
