package com.balloonmail.app.balloonmailapp.models;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Reem Hamdy on 4/26/2016.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?> [] classes = new Class[]{User.class, ReceivedBalloon.class, SentBalloon.class, LikedBalloon.class};
    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile(new File("C:\\Users\\Reem Hamdy\\Git\\BalloonMail\\android app" +
                "\\app\\src\\main\\res\\raw\\ormlite_config.txt")
                , classes);
    }
}
