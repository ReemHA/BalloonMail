package com.balloonmail.app.balloonmailapp.manager.creep;

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
public class CreepRequester {
    public static void requestCreepToServer(final ICreepableUI creepableCard) {
        final ICreepableModel creepableBalloon = (ICreepableModel) creepableCard.getBalloon();
        final ImageButton creepButton = creepableCard.getCreepButton();
        final CreepHandler creepHandler = new CreepHandler(creepableBalloon, creepButton);
        Context context = creepableCard.getCurrentContext();
        ReusableAsync<Void> task = new ReusableAsync(context)
                .post("/balloons/creep")
                .bearer(Global.getApiToken(context))
                .addData("balloon_id", Integer.toString(creepableCard.getBalloon().getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            // get whether the balloon is creeped or not.
                            int isCreeped = creepableBalloon.getIsCreeped();
                            if (isCreeped == 0){
                                creepableBalloon.setIsCreeped(1);
                            }else {
                                creepableBalloon.setIsCreeped(0);
                            }
                            return null;
                        }
                    })
                .onPost(new PostHandler<Void>() {
                    @Override
                    public void handle(Void data) {
                        creepHandler.handleButtonUI();
                    }
                });
                task.send();
    }
}
