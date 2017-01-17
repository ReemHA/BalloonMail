package com.balloonmail.app.balloonmailapp.manager;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.manager.creep.CreepHandler;
import com.balloonmail.app.balloonmailapp.manager.creep.CreepRequester;
import com.balloonmail.app.balloonmailapp.manager.creep.ICreepableModel;
import com.balloonmail.app.balloonmailapp.manager.creep.ICreepableUI;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableModel;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableUI;
import com.balloonmail.app.balloonmailapp.manager.like.LikeHandler;
import com.balloonmail.app.balloonmailapp.manager.like.LikeRequester;
import com.balloonmail.app.balloonmailapp.manager.refill.IRefillableModel;
import com.balloonmail.app.balloonmailapp.manager.refill.IRefillableUI;
import com.balloonmail.app.balloonmailapp.manager.refill.RefillHandler;
import com.balloonmail.app.balloonmailapp.manager.refill.RefillRequester;
import com.balloonmail.app.balloonmailapp.utilities.Global;

/**
 * Created by Reem Hamdy on 1/13/2017.
 */
public class AppManager {
    private static AppManager manager;
    private LikeRequester likeRequester;
    private RefillRequester refillRequester;
    private LikeHandler likeHandler;
    private RefillHandler refillHandler;
    private CreepHandler creepHandler;

    private CreepRequester creepRequester;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (manager == null) {
            manager = new AppManager();
        }
        return manager;
    }

    public void like(ILikeableUI likeableCard) {
        likeRequester = new LikeRequester();
        likeRequester.requestLikeToServer(likeableCard);
    }

    public void refill(IRefillableUI refillableCard) {
        /**
         * if the balloon is refilled show error message to user.
         */
        Context context = refillableCard.getCurrentContext();
        if (refillHandler.isRefilled()) {
            if (!Global.isConnected(context)) {
                Global.showMessage(context, "No Internet Connection",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } else {
                Global.showMessage(context, "Refill button is clicked twice.",
                        Global.ERROR_MSG.REFILL_BUTTON_CLICKED_TWICE.getMsg());
            }
        } else {
            refillRequester = new RefillRequester();
            refillRequester.requestRefillToServer(refillableCard);
        }
    }

    public void creep(ICreepableUI creepableCard) {
        /**
         * if the balloon is creeped show error message to user.
         */
        Context context = creepableCard.getCurrentContext();
        if (creepHandler.isCreeped()) {
            if (!Global.isConnected(context)) {
                Global.showMessage(context, "No Internet Connection",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } else {
                Global.showMessage(context, "Creep button is clicked twice.",
                        Global.ERROR_MSG.CREEP_BUTTON_CLICKED_TWICE.getMsg());
            }
        } else {
            creepRequester = new CreepRequester();
            creepRequester.requestCreepToServer(creepableCard);
        }
    }

    public void instantiateSentimentState(double sentimentValue, View sentimentIndication){
        ActionHandler.changeColorOfSentimentIndication(sentimentValue, sentimentIndication);
    }
    public void instantiateLikeButtonState(ILikeableModel balloon, ImageButton likeButton) {
        likeHandler = new LikeHandler(balloon, likeButton);
        likeHandler.handleButtonUI();
    }

    public void instantiateRefillButtonState(IRefillableModel balloon, ImageButton refillButton) {
        refillHandler = new RefillHandler(balloon, refillButton);
        refillHandler.handleButtonUI();
    }

    public void instantiateCreepButtonState(ICreepableModel balloon, ImageButton creepButton) {
        creepHandler = new CreepHandler(balloon, creepButton);
        creepHandler.handleButtonUI();
    }

}
