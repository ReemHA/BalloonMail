package com.balloonmail.app.balloonmailapp.controller;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Reem Hamdy on 4/30/2016.
 */
public class CustomJsonRequest extends Request<JSONObject> {
    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;
    public CustomJsonRequest(int method, String url, Map<String, String> params,
                             Response.Listener<JSONObject> responseListener, Response.ErrorListener listener) {
        super(method, url, listener);
        this.listener = responseListener;
        this.params = params;

    }

    @Override
    public Map<String, String> getParams() throws AuthFailureError{
        return params;
    };

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
