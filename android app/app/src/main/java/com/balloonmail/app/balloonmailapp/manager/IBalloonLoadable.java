package com.balloonmail.app.balloonmailapp.manager;
import com.balloonmail.app.balloonmailapp.models.Balloon;

import it.gmariotti.cardslib.library.internal.Card;

/**
 * Created by Reem Hamdy on 1/18/2017.
 * This interface contains all the functions related to loading of balloons.
 */
public interface IBalloonLoadable {
    void loadBalloons();
    Card createCard(Balloon balloon);
}
