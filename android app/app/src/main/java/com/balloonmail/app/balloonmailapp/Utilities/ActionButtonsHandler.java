package com.balloonmail.app.balloonmailapp.utilities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.CardReceived;
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

    public static void changeStateOfLikeBtn(Balloon balloon, ImageButton likeBtn) {
        if(balloon instanceof ReceivedBalloon){
            if (((ReceivedBalloon)balloon).getIs_liked() == 0) {
                likeOff(likeBtn);
            } else {
                likeOn(likeBtn);
            }
        }else if(balloon instanceof LikedBalloon){
            initializeStateOfLikeBtn(likeBtn);
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

    //OnClick Functions of Action Buttons to handle the messages of more than one click and no network connection

    public static void onClickOfRefillButton(final Balloon _balloonToBeRefilled, Context context, final ImageButton refillBtn){
        int isRefilled = 0;
        if(_balloonToBeRefilled instanceof ReceivedBalloon){
            isRefilled = ((ReceivedBalloon) _balloonToBeRefilled).getIs_refilled();
        }else if(_balloonToBeRefilled instanceof LikedBalloon){
            isRefilled = ((LikedBalloon) _balloonToBeRefilled).getIs_refilled();
        }
        if (isRefilled == 0) {
            Log.d(CardReceived.class.getSimpleName(), " 1 refill is clicked");
            ActionButtonsHandler.requestRefillToServer(_balloonToBeRefilled, context, refillBtn);
            Log.d(CardReceived.class.getSimpleName(), "refill change color");
        } else {
            // in case no internet connection the server conn fail msg should appear
            if (!Global.isConnected(context)) {
                Global.showMessage(context, "No internet connection",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } else {
                Global.showMessage(context, "refill btn clicked twice",
                        Global.ERROR_MSG.REFILL_REQ_FAIL.getMsg());
            }
        }
    }

    public static void onClickOfCreepButton(final Balloon _balloonToBeCreeped, Context context, final ImageButton creepBtn){
        int isCreeped = 0;
        if(_balloonToBeCreeped instanceof ReceivedBalloon){
            isCreeped = ((ReceivedBalloon) _balloonToBeCreeped).getIs_creeped();
        }else if(_balloonToBeCreeped instanceof LikedBalloon){
            isCreeped = ((LikedBalloon) _balloonToBeCreeped).getIs_creeped();
        }
        if (isCreeped == 0) {
            ActionButtonsHandler.requestCreepToServer(_balloonToBeCreeped, context, creepBtn);
        } else {

            // in case no internet connection the server conn fail msg should appear
            if (!Global.isConnected(context)) {
                Global.showMessage(context, "No internet connection",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } else {
                Global.showMessage(context, "creep btn clicked twice",
                        Global.ERROR_MSG.CREEP_REQ_FAIL.getMsg());
            }
        }
    }

    public static void onClickOfLikeButton(final Balloon _balloonToBeLiked, Context context, final ImageButton likeBtn){
        ActionButtonsHandler.requestLikeToServer(_balloonToBeLiked, context, likeBtn);
    }

    //Server Actions

    private static void requestLikeToServer(final Balloon _balloonToBeLiked, Context context, final ImageButton likeBtn) {
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

    private static void requestRefillToServer(final Balloon _balloonToBeRefilled, Context context, final ImageButton refillBtn) {
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

    private static void requestCreepToServer(final Balloon _balloonToBeCreeped, Context context, final ImageButton creepBtn) {
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
