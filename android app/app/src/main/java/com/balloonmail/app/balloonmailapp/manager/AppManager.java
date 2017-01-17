package com.balloonmail.app.balloonmailapp.manager;

import com.balloonmail.app.balloonmailapp.manager.creep.CreepRequester;
import com.balloonmail.app.balloonmailapp.manager.like.ILikeableUI;
import com.balloonmail.app.balloonmailapp.manager.like.LikeRequester;
import com.balloonmail.app.balloonmailapp.manager.refill.RefillRequester;

/**
 * Created by Reem Hamdy on 1/13/2017.
 */
public class  AppManager{
    private static AppManager manager;
    private LikeRequester likeRequester;
    private RefillRequester refillRequester;
    private CreepRequester creepRequester;
    private AppManager() {
    }

    public static AppManager getInstance(){
        if (manager == null){
            manager = new AppManager();
        }
        return manager;
    }

    public void like(ILikeableUI likeableCard){
        likeRequester = new LikeRequester();
        likeRequester.requestLikeToServer(likeableCard);
    }

    public void refill(){

    }

    public void creep(){

    }
}
