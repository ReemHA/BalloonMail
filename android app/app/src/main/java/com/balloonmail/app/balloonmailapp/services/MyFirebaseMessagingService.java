package com.balloonmail.app.balloonmailapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.activities.MailsTabbedActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Reem Hamdy on 7/14/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    private static final int RC = 0;
    private static final int NOTI_RC = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String typeOfNotification = data.get("type");
        String msg;
        switch (typeOfNotification) {
            case "REC":
                msg = "You received a balloon from "+
                        getCountryName(getApplicationContext(), Double.valueOf(data.get("lng")), Double.valueOf(data.get("lat")))
                        + ".";
            case "CRP":
                msg = data.get("creeps") + " creeped your balloon!";
                break;
            case "RFL":
                msg = "You have got " + data.get("refills") + " new refills.";
                break;
            default:
                msg = "Hello";
        }
        sendNotification(msg);
    }

    private void sendNotification(String message) {
        Intent intent = new Intent(this, MailsTabbedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                RC, intent, 0);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo_balloonmail_app)
                .setContentTitle("BalloonMail")
                .setContentText(message)
                .setSound(uri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTI_RC, notificationBuilder.build());
    }

    private static String getCountryName(Context context, double lon, double lat) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return "Oz";
        } catch (IOException ignored) {
            return "Camelot";
        }
    }
}
