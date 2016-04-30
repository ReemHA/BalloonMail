package com.balloonmail.app.balloonmailapp.controller;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

/**
 * Created by Reem Hamdy on 4/29/2016.
 */
public class RequestQueueSingleton {
    private static RequestQueueSingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;
    public static final String TAG = "default";

    public RequestQueueSingleton(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new RequestQueueSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue(){
        if (mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request, String tag) throws AuthFailureError {
        request.setTag(TextUtils.isEmpty(tag)? TAG : tag);
        VolleyLog.d("Adding request to request queue: ", request.getBody());
        getRequestQueue().add(request);
    }

    public void cancelRequestsWithTag(String tag){
        if (mRequestQueue != null){
            getRequestQueue().cancelAll(tag);
        }
    }
}
