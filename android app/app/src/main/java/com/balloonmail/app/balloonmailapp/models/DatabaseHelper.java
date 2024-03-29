package com.balloonmail.app.balloonmailapp.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.balloonmail.app.balloonmailapp.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "balloonmail.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<ReceivedBalloon, Integer> receivedBalloonDao = null;
    private Dao<SentBalloon, Integer> sentBalloonDao = null;
    private Dao<LikedBalloon, Integer> likedBalloonDao = null;

    private RuntimeExceptionDao<ReceivedBalloon, Integer> receivedBalloonRuntimeExceptionDao = null;
    private RuntimeExceptionDao<SentBalloon, Integer> sentBalloonRuntimeExceptionDao = null;
    private RuntimeExceptionDao<LikedBalloon, Integer> likedBalloonRuntimeExceptionDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        // create tables
        try {
            TableUtils.createTable(connectionSource, LikedBalloon.class);
            TableUtils.createTable(connectionSource, ReceivedBalloon.class);
            TableUtils.createTable(connectionSource, SentBalloon.class);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        try {
            // drop tables
            TableUtils.dropTable(connectionSource, LikedBalloon.class, true);
            TableUtils.dropTable(connectionSource, ReceivedBalloon.class, true);
            TableUtils.dropTable(connectionSource, SentBalloon.class, true);

            // re-create database
            onCreate(sqLiteDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Dao<ReceivedBalloon, Integer> getReceivedBalloonDao() throws SQLException {
       if (receivedBalloonDao == null){
           receivedBalloonDao = getDao(ReceivedBalloon.class);
       }
        return receivedBalloonDao;
    }

    public RuntimeExceptionDao<ReceivedBalloon, Integer> getReceivedBalloonRuntimeExceptionDao() throws SQLException {
        if (receivedBalloonRuntimeExceptionDao == null){
            receivedBalloonRuntimeExceptionDao = getDao(ReceivedBalloon.class);
        }
        return receivedBalloonRuntimeExceptionDao;
    }

    public Dao<SentBalloon, Integer> getSentBalloonDao() throws SQLException{
        if (sentBalloonDao == null){
            sentBalloonDao = getDao(SentBalloon.class);
        }

        return sentBalloonDao;
    }

    public RuntimeExceptionDao<SentBalloon, Integer> getSentBalloonRuntimeExceptionDao() throws SQLException{
        if (sentBalloonRuntimeExceptionDao == null){
            sentBalloonRuntimeExceptionDao = (RuntimeExceptionDao<SentBalloon, Integer>)
                    new RuntimeExceptionDao<>(getDao(SentBalloon.class));
        }

        return sentBalloonRuntimeExceptionDao;
    }
    public Dao<LikedBalloon, Integer> getLikedBalloonDao() throws SQLException{
        if (likedBalloonDao == null){
            likedBalloonDao = getDao(LikedBalloon.class);
        }

        return likedBalloonDao;
    }

    public RuntimeExceptionDao<LikedBalloon, Integer> getLikedBalloonRuntimeExceptionDao() throws SQLException{
        if (likedBalloonRuntimeExceptionDao == null){
            likedBalloonRuntimeExceptionDao = getDao(LikedBalloon.class);
        }

        return likedBalloonRuntimeExceptionDao;
    }
    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        receivedBalloonDao = null;
        receivedBalloonRuntimeExceptionDao = null;
        sentBalloonDao = null;
        sentBalloonRuntimeExceptionDao = null;
        likedBalloonDao = null;
        likedBalloonRuntimeExceptionDao = null;
    }
}

