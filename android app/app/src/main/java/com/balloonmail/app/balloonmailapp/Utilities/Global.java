package com.balloonmail.app.balloonmailapp.Utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Reem Hamdy on 4/24/2016.
 */
public class Global {
    //public static final String SERVER_URL = "http://10.0.2.2:8080";
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
    public static final String PREF_USER_IS_CREATED = "created";

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
    ;
    public static Retrofit retrofitNoClient = new Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static Retrofit retrofit = retrofitNoClient;

    public static Retrofit getRetrofit(final Context context) {

        //get the shared preference file
        final SharedPreferences sharedPreferences =
                context.getSharedPreferences(Global.USER_INFO_PREF_FILE, context.MODE_PRIVATE);

        //get the api token from shared preferences
        final String key = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");

        //does the key exist?
        if (key != "") {
            if (retrofit == retrofitNoClient) {
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Interceptor.Chain chain) throws IOException {
                        Request original = chain.request();

                        Request request = original.newBuilder()
                                .header("Content-Type", "application/json")
                                .header("authorization", "Bearer " + key)
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                });
                //build the retrofit
                retrofit = new Retrofit.Builder()
                        .baseUrl(SERVER_URL)
                        .client(httpClient.build())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
        } else {
            retrofit = retrofitNoClient;
        }
        return retrofit;
    }
}
