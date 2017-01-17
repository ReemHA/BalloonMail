package com.balloonmail.app.balloonmailapp.manager.like;

import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.manager.ActionHandler;

/**
 * Created by Reem Hamdy on 1/14/2017.
 */
public class LikeHandler implements ActionHandler{
    private ILikeableModel likeableBalloon;
    private ImageButton likeButton;

    public LikeHandler(ILikeableModel likeableBalloon, ImageButton likeButton) {
        this.likeableBalloon = likeableBalloon;
        this.likeButton = likeButton;
    }

    @Override
    public void handleButtonUI() {
        if (likeableBalloon.getIsLiked()  == 0) {
            //turn image to grey.
            likeButton.setImageResource(R.drawable.ic_like_grey_24px);

        } else {
            //turn image to colored.
            likeButton.setImageResource(R.drawable.ic_like_clicked_24px);

        }
    }
}
