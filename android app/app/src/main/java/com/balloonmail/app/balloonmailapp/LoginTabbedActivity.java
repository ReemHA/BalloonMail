package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.User;
import com.balloonmail.app.balloonmailapp.rest.RInterface;
import com.balloonmail.app.balloonmailapp.rest.model.ServerRequest;
import com.balloonmail.app.balloonmailapp.rest.model.ServerResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import org.json.JSONException;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginTabbedActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int RC_SIGN_IN = 9001;
    private static final String SIGN_IN_ERROR_TAG = "handle sign in";
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private GoogleApiClient googleApiClient;
    private DatabaseHelper dbHelper;
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tabbed);

        if (!isSignedOut()) {
            Intent intent = new Intent(LoginTabbedActivity.this, HomeActivity.class);
            startActivity(intent);
            LoginTabbedActivity.this.finish();
        }


        // Configure sign in to request the user's id token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Global.SERVER_CLIENT_ID)
                .requestProfile()
                .build();

        // GoogleApiClient is main entry for Google Play services integration. Build GoogleApiClient to access the options specified by gso
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
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
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        Log.d(Global.LOG_TAG, "onStart");
        if (isSignedOut()) {
            logOutFromGoogleAccount();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
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
                }
            }
        }
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
    private void handleSignIntent(GoogleSignInResult result) throws JSONException {
        if (result.isSuccess()) {

            // get the idToken of the user
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();

            // get the user name
            String userName = account.getDisplayName();

            // get the user email
            String userEmail = account.getEmail();

            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult succeeded");

            // send the idToken and username to the app server
            sendDataToServer(idToken, userName, userEmail);
        } else {
            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult failed");
        }
    }


    private void sendDataToServer(String idToken, final String userName, final String userEmail) {
        ServerRequest body = new ServerRequest(userName, idToken);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RInterface request = retrofit.create(RInterface.class);
        Call<ServerResponse> call = request.postData(body);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse jsonResponse = response.body();

                // checks if an error is in the response
                if (!jsonResponse.toString().contains("error")) {
                    String api_token;

                    // get api_token of the user from the response
                    api_token = jsonResponse.getApi_token();

                    Intent intent = new Intent(LoginTabbedActivity.this, HomeActivity.class);
                    startActivity(intent);
                    LoginTabbedActivity.this.finish();

                    dbHelper = OpenHelperManager.getHelper(LoginTabbedActivity.this, DatabaseHelper.class);
                    Dao<User, String> userDao = null;
                    try {
                        userDao = dbHelper.getUserDao();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    // checks if he is a new user
                    if (jsonResponse.isCreated()) {

                        try {
                            // insert a new user record
                            userDao.create(new User(api_token, userName, userEmail));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            // update the api_token field in the record of the user with specified email
                            UpdateBuilder<User, String> updateBuilder = userDao.updateBuilder();
                            updateBuilder.where().eq("user_email", userEmail);
                            updateBuilder.updateColumnValue("api_token", api_token);
                            updateBuilder.update();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    OpenHelperManager.releaseHelper();
                } else {
                    Log.d("Response from Server: ", jsonResponse.getError());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (t.getMessage() != null) {
                    Log.d("Error", t.getMessage());
                }
            }
        });

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), connectionResult.getErrorMessage(), Toast.LENGTH_LONG).show();

    }

    private boolean isSignedOut() {
        Bundle extras = getIntent().getExtras();
        boolean isSignedOut;
        if (extras != null) {
            isSignedOut = true;
        } else {
            isSignedOut = false;
        }
        Log.d(Global.LOG_TAG, "isSignedOut:" + isSignedOut);

        return isSignedOut;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(Global.LOG_TAG, "onConnected:" + isSignedOut());
        if (isSignedOut() && i < 1) {
            logOutFromGoogleAccount();
            i++;
        }
    }

    private void logOutFromGoogleAccount() {
        if (googleApiClient.isConnected()) {
            googleApiClient.clearDefaultAccountAndReconnect();
            Log.d(Global.LOG_TAG, "logOutFromGoogleAccount: account cleared");
            googleApiClient.connect();
            signOut();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                dbHelper = OpenHelperManager.getHelper(getApplicationContext(), DatabaseHelper.class);
                try {
                    Dao<User, String> userDao = dbHelper.getUserDao();
                    DeleteBuilder<User, String> deleteBuilder = userDao.deleteBuilder();
                    deleteBuilder.where().isNotNull("api_token");
                    deleteBuilder.delete();
                    Log.d(Global.LOG_TAG, "signOut status:" + status.isSuccess());

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

}