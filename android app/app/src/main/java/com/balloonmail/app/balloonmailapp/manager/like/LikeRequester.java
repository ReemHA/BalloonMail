package com.balloonmail.app.balloonmailapp.manager.like;

import android.content.Context;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.utilities.Global;

import org.json.JSONObject;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class LikeRequester {
    public void requestLikeToServer(final ILikeableUI likeableCard) {
        final ILikeableModel likeableBalloon = (ILikeableModel) likeableCard.getBalloon();
        final ImageButton likeButton = likeableCard.getLikeButton();
        final LikeHandler likeHandler = new LikeHandler(likeableBalloon, likeButton);
        Context currentContext = likeableCard.getCurrentContext();
        ReusableAsync<Void> task = new ReusableAsync<>(currentContext)
                .bearer(Global.getApiToken(currentContext))
                .post("/balloons/like")
                .addData("balloon_id", Integer.toString(likeableCard.getBalloon().getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) {
                            // get whether the balloon is already liked or not.
                            int isLiked = likeableBalloon.getIsLiked();
                            if (isLiked == 1){
                                likeableBalloon.setIsLiked(0);
                            }else{
                                likeableBalloon.setIsLiked(1);
                            }
                            // return the value to onPost to do any changes in UI.
                            return null;
                        }
                    })
                    .onPost(new PostHandler<Void>() {
                        @Override
                        public void handle(Void data) {
                            likeHandler.handleButtonUI();
                        }
                    });
        task.send();
    }

}
