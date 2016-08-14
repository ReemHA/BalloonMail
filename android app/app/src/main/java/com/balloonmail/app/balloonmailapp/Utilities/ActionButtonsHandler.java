package com.balloonmail.app.balloonmailapp.utilities;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Dalia on 8/10/2016.
 */
public class ActionButtonsHandler {
    public static void changeColorOfSentimentIndication(double sentiment, View view){
        if(sentiment < 0){
            view.setBackgroundResource(R.color.red);
        }else if(sentiment > 0){
            view.setBackgroundResource(R.color.green);
        }else{
            view.setBackgroundResource(R.color.colorPrimary);
        }
    }

    public static void changeStateOfRefillBtn(Balloon balloon, ImageButton refillBtn) {
        if(balloon instanceof ReceivedBalloon){
            if (((ReceivedBalloon) balloon).getIs_refilled() == 0) {
                refillOff(refillBtn);
            } else {
                refillOn(refillBtn);
            }
        }else if(balloon instanceof LikedBalloon){
            if (((LikedBalloon) balloon).getIs_refilled() == 0) {
                refillOff(refillBtn);
            } else {
                refillOn(refillBtn);
            }
        }
    }

    private static void refillOn(ImageButton refillBtn){
        refillBtn.setImageResource(R.drawable.ic_refill_primary_24px);
    }
    private static void refillOff(ImageButton refillBtn){
        refillBtn.setImageResource(R.drawable.ic_refill_grey_24px);
    }

    public static void changeStateOfLikeBtn(ReceivedBalloon balloon, ImageButton likeBtn) {
        if (balloon.getIs_liked() == 0) {
            likeOff(likeBtn);
        } else {
            likeOn(likeBtn);
        }
    }

    public static void initializeStateOfLikeBtn(ImageButton likeBtn) {
        likeOn(likeBtn);
    }

    private static void likeOn(ImageButton likeBtn){
        likeBtn.setImageResource(R.drawable.ic_like_clicked_24px);
    }
    private static void likeOff(ImageButton likeBtn){
        likeBtn.setImageResource(R.drawable.ic_like_grey_24px);
    }

    public static void changeStateOfCreepBtn(Balloon balloon, ImageButton creepBtn) {
        if(balloon instanceof ReceivedBalloon){
            if (((ReceivedBalloon) balloon).getIs_creeped() == 0) {
                creepOff(creepBtn);
            } else {
                creepOn(creepBtn);
            }
        }else if(balloon instanceof LikedBalloon){
            if (((LikedBalloon) balloon).getIs_creeped() == 0) {
                creepOff(creepBtn);
            } else {
                creepOn(creepBtn);
            }
        }
    }

    private static void creepOn(ImageButton creepBtn){
        creepBtn.setImageResource(R.drawable.ic_creepy_clicked_24px);
    }
    private static void creepOff(ImageButton creepBtn){
        creepBtn.setImageResource(R.drawable.ic_creepy_grey_24px);
    }


    //Server Actions

    public static void requestLikeToServer(final Balloon _balloonToBeLiked, Context context, final ImageButton likeBtn) {
        ReusableAsync<Void> task = new ReusableAsync<>(context)
                .bearer(Global.getApiToken(context))
                .post("/balloons/like")
                .addData("balloon_id", Integer.toString(_balloonToBeLiked.getBalloon_id()));

        if(_balloonToBeLiked instanceof ReceivedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) {
                            ((ReceivedBalloon) _balloonToBeLiked).onLikeClick();
                            return null;
                        }
                    })
                    .onPost(new PostHandler<Void>() {
                        @Override
                        public void handle(Void data) {
                            ActionButtonsHandler.changeStateOfLikeBtn((ReceivedBalloon) _balloonToBeLiked, likeBtn);
                        }
                    });
        }else if(_balloonToBeLiked instanceof LikedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            ((LikedBalloon) _balloonToBeLiked).onLikeClick();
                            return null;
                        }
                    });
        }
        task.send();
    }

    public static void requestRefillToServer(final Balloon _balloonToBeRefilled, Context context, final ImageButton refillBtn) {
        ReusableAsync task = new ReusableAsync(context)
                .post("/balloons/refill")
                .bearer(Global.getApiToken(context))
                .addData("balloon_id", Integer.toString(_balloonToBeRefilled.getBalloon_id()));

        if(_balloonToBeRefilled instanceof ReceivedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            ((ReceivedBalloon) _balloonToBeRefilled).onRefillClick();
                            return null;
                        }
                    });
        }else if(_balloonToBeRefilled instanceof LikedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            ((LikedBalloon) _balloonToBeRefilled).onRefillClick();
                            return null;
                        }
                    });
        }
        task
                .onPost(new PostHandler() {
                    @Override
                    public void handle(Object data) {
                        ActionButtonsHandler.changeStateOfRefillBtn(_balloonToBeRefilled, refillBtn);
                    }
                })
                .send();
    }

    public static void requestCreepToServer(final Balloon _balloonToBeCreeped, Context context, final ImageButton creepBtn) {
        ReusableAsync task = new ReusableAsync(context)
                .post("/balloons/creep")
                .bearer(Global.getApiToken(context))
                .addData("balloon_id", Integer.toString(_balloonToBeCreeped.getBalloon_id()));

        if(_balloonToBeCreeped instanceof ReceivedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            ((ReceivedBalloon) _balloonToBeCreeped).onCreepClick();
                            return null;
                        }
                    });
        }else if(_balloonToBeCreeped instanceof LikedBalloon){
            task
                    .onSuccess(new SuccessHandler<Void>() {
                        @Override
                        public Void handle(JSONObject data) throws JSONException {
                            ((LikedBalloon) _balloonToBeCreeped).onCreepClick();
                            return null;
                        }
                    });
        }

        task
                .onPost(new PostHandler<Void>() {
                    @Override
                    public void handle(Void data) {
                        ActionButtonsHandler.changeStateOfCreepBtn(_balloonToBeCreeped, creepBtn);
                    }
                })
                .send();
    }
}
