package com.balloonmail.app.balloonmailapp.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Reem Hamdy on 4/24/2016.
 */
public class Global {
    //public static final String SERVER_URL = "http://6.6.6.149:8080";
    public static final String SERVER_URL = "http://app2-balloonmail.rhcloud.com";
    public static final String SERVER_CLIENT_ID =
            "113808451021-5bbh4fp49eo7tdq8j93arot30tt49q8j.apps.googleusercontent.com";
    public static final String ARG_MAILS_TABBED_TAG = "tabbedPage";
    public static final int SENT_TABBED_PAGE = 0;
    public static final int RECEIVED_TABBED_PAGE = 1;
    public static final int LIKES_TABBED_PAGE = 2;
    public static final String LOG_TAG = "DOWN";
    public static final String USER_INFO_PREF_FILE = "PersonalInfo";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_API_TOKEN = "api_token";
    public static final String PREF_USER_LAT = "lat";
    public static final String PREF_USER_LNG = "lon";
    public static final boolean inDebug = false;
    private static final double DUMMY_LAT = 51.507351;
    private static final double DUMMY_LNG = -0.127758;

    public enum ERROR_MSG {
        SERVER_CONN_FAIL("Couldn't connect to server."),
        CREEP_REQ_FAIL("You already creeped this balloon once."),
        REFILL_REQ_FAIL("You can't refill this balloon again."),
        DEFAULT_LOCATION_WARNING("A dummy location will be used."),
        NETWORK_CONN_FAIL("No internet connection.");

        String msg;

        ERROR_MSG(String msg) {
            this.msg = msg;
        }

        public String getMsg() {
            return msg;
        }
    }

    public static boolean isConnected(Context context) {
        // check the state of network connectivity
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // get an instance of the current active network
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;

        }
        return false;
    }

    public static BalloonHolder balloonHolder = BalloonHolder.getInstance();

    public static void showMessage(Context context, String debug_message, String release_message) {
        if (inDebug) {
            Toast.makeText(context, debug_message, Toast.LENGTH_LONG).show();
        } else {
            Snackbar snackbar = Snackbar.make(((Activity) context).findViewById(android.R.id.content),
                    release_message, Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    public static Date getDateFromString(String date) {
        //Assuming date format yyyy-mm-dd hh:MM:ss
        String[] token = date.split(" +");
        if (token.length != 2)
            return null;
        String[] _date = token[0].split("-");
        if (_date.length != 3)
            return null;
        String[] time = token[1].split(":");
        if (time.length != 3)
            return null;
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        c.set(Integer.parseInt(_date[0]),
                Integer.parseInt(_date[1]),
                Integer.parseInt(_date[2]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[0]),
                Integer.parseInt(time[0])
        );
        return c.getTime();
    }

    public static String getApiToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");
    }

    public static double[] getDummyLocation(){
        double[] loc = new double[2];
        loc[0] = DUMMY_LAT;
        loc[1] = DUMMY_LNG;
        return loc;
    }


}
