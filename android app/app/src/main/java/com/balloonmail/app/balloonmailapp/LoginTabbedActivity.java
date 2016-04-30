package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.controller.RequestQueueSingleton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginTabbedActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String SIGN_IN_ERROR_TAG = "handle sign in";
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private static final String VOLLEY_TAG = "VOLLEY TAG";
    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tabbed);

        // Configure sign in to request the user's id token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Global.SERVER_CLIENT_ID)
                .requestProfile()
                .build();

        // GoogleApiClient is main entry for Google Play services integration. Build GoogleApiClient to access the options specified by gso
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton google_sign_in = (SignInButton) findViewById(R.id.login_google_button);
        google_sign_in.setSize(SignInButton.SIZE_WIDE);
        google_sign_in.setScopes(gso.getScopeArray());
        google_sign_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.login_google_button:
                        // attempts signing in
                        signIn();
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check the request code returned
        if (requestCode == RC_SIGN_IN) {

            // check the result code returned
            if (resultCode == RESULT_OK) {

                // store returned data
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                try {
                    // handle returned data
                    handleSignIntent(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (AuthFailureError authFailureError) {
                    authFailureError.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();
    }

    private void signIn() {

        // check whether a network connection is available or not
        if (checkNetworkConnection()) {

            // send an intent with the request to get the user's data
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {

            // show a toast if no network connection is available
            Toast.makeText(getApplicationContext(), NETWORK_CONNECTION_MSG, Toast.LENGTH_LONG).show();
        }
    }

    // check network connection
    private boolean checkNetworkConnection() {

        // check the state of network connectivity
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // get an instance of the current active network
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

    // handle the data returned from onActivityResult
    private void handleSignIntent(GoogleSignInResult result) throws JSONException, AuthFailureError {
        if (result.isSuccess()) {

            // get the idToken of the user
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();

            // get the username of the user
            String userName = account.getDisplayName();

            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult succeeded");

            // send the idToken and username to the app server
            sendDataToServer(idToken, userName);
        } else {
            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult failed");
        }
    }


    private void sendDataToServer(String idToken, String userName) throws JSONException, AuthFailureError {

        // store the idToken and username in a JSONObject
        final Map<String, String> params = new HashMap<>();
        params.put("access_token", idToken);
        params.put("user_name", userName);

        // send the json object as a request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                Global.SERVER_URL + "/token/google",
                new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonResponse = response.getJSONObject("result");

                    // checks if an error is in the response
                    if (!jsonResponse.has("error")) {
                        String userID, api_token;

                        // get api_token of the user from the response
                        api_token = jsonResponse.getString("api_token");

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        // checks if he is a new user
                        if (jsonResponse.getBoolean("created")) {
                            // insert a new user record
                            //insertNewUserData(userID, api_token);

                        } else {
                            // save api_token in the record of the user of this userID
                            //insertApiTokenToUser(userID, api_token);
                        }
                    } else {
                        Log.d("Response from Server: ", jsonResponse.getString("error"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null) {
                    VolleyLog.d(error.getMessage());
                }else{
                    VolleyLog.d(VOLLEY_TAG, "No error msg");
                }
                // show a toast in case of an error returned from the server
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                // set request http header
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        // add the request to the queue to be executed and set its tag
        RequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest, "sign in");
    }

}