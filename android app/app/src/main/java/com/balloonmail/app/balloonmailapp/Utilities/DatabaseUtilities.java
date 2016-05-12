package com.balloonmail.app.balloonmailapp.Utilities;

import android.content.Context;

import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Reem Hamdy on 5/9/2016.
 */
public class DatabaseUtilities {
    DatabaseHelper helper;

    public void createDatabase(Context context){
        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            TableUtils.createTable(helper.getConnectionSource(), SentBalloon.class);
            TableUtils.createTable(helper.getConnectionSource(), ReceivedBalloon.class);
            TableUtils.createTable(helper.getConnectionSource(), LikedBalloon.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public void resetDatabase(Context context) {
        Dao<SentBalloon, Integer> sentBalloons;
        Dao<ReceivedBalloon, Integer> receivedBalloons;
        Dao<LikedBalloon, Integer> likedBalloons;

        helper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        try {
            sentBalloons = helper.getSentBalloonDao();
            receivedBalloons = helper.getReceivedBalloonDao();
            likedBalloons = helper.getLikedBalloonDao();
            TableUtils.dropTable(sentBalloons.getConnectionSource(), SentBalloon.class, false);
            TableUtils.dropTable(receivedBalloons.getConnectionSource(), ReceivedBalloon.class, false);
            TableUtils.dropTable(likedBalloons.getConnectionSource(), LikedBalloon.class, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return;
    }

    public void getSentBalloonFromServer(){

    }
    public void getReceivedBalloonFromServer(){

    }
    public void getLikedBalloonFromServer(){

    }
}
