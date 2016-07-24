package com.balloonmail.app.balloonmailapp.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.utilities.DatabaseUtilities;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginTabbedActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    private int i = 0;
    DatabaseUtilities databaseUtilities;
    SharedPreferences sharedPreferences;
    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private Location mLastLocation;
    private static final int RC_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tabbed);

        // get api_token from the shared preference
        sharedPreferences = getSharedPreferences(Global.USER_INFO_PREF_FILE, MODE_PRIVATE);
        String api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");

        databaseUtilities = new DatabaseUtilities();

        // Configure sign in to request the user's id token
        gso = buildSignInOptions();

        // GoogleApiClient is main entry for Google Play services integration. Build GoogleApiClient to access the options specified by gso
        googleApiClient = buildApiClient();

        // Get api_token from the shared preference
        if (!isSignedOut() && api_token != "") {
            getLocation();
            Intent intent = new Intent(LoginTabbedActivity.this, MailsTabbedActivity.class); //HomeActivity
            startActivity(intent);
            finish();
        }

        // create a new blank database for the new signed in user
        if (isSignedOut()) {
            databaseUtilities.createDatabase(LoginTabbedActivity.this);
        }

        getLocation();

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

    private GoogleSignInOptions buildSignInOptions() {
        return new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Global.SERVER_CLIENT_ID)
                .requestProfile()
                .requestEmail()
                .build();
    }

    private GoogleApiClient buildApiClient() {
        return new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int error_code = apiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
        if ((error_code == ConnectionResult.SERVICE_MISSING) ||
                (error_code == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) ||
                (error_code == ConnectionResult.SERVICE_DISABLED)) {
            apiAvailability.getErrorDialog(this, error_code, RC_SIGN_IN);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        if (isSignedOut()) {
            logOutFromGoogleAccount();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
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
        }else if (requestCode == RC_LOCATION){
            if (resultCode == RESULT_OK){
                getLocation();
            }
        }
    }

    private void signIn() {

        // check whether a network connection is available or not
        if (Global.isConnected(this)) {

            // send an intent with the request to get the user's data
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {

            // show a message if the device is not connected to the internet
            Global.showMessage(getApplicationContext(), NETWORK_CONNECTION_MSG,
                    Global.ERROR_MSG.NETWORK_CONN_FAIL.getMsg());
        }
    }

    // handle the data returned from onActivityResult
    private void handleSignIntent(GoogleSignInResult result) throws JSONException {

        if (result.isSuccess()) {

            // get the idToken of the user
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();


            // get the user name
            String userName = account.getDisplayName();
            sharedPreferences.edit().putString(Global.PREF_USER_NAME, userName).commit();

            // get the user email
            String userEmail = account.getEmail();
            sharedPreferences.edit().putString(Global.PREF_USER_EMAIL, userEmail).commit();


            if (mLastLocation != null) {
                String lat = String.valueOf(mLastLocation.getLatitude());
                String lng = String.valueOf(mLastLocation.getLongitude());

                // send the idToken and username to the app server
                sendDataToServer(idToken, userName, userEmail, lat, lng);
            } else {
                // show message if location service is turned off
                final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setMessage("Your GPS seems to be disabled, do you want to enable it?");
                alertDialog.setCancelable(false);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                                RC_LOCATION);
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        } else {

            // show message if result returned from Google isn't successfully returned
            Global.showMessage(this, "Google didn't successfully returned token",
                    Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
        }
    }


    private void sendDataToServer(String idToken, final String userName, final String userEmail, String lat, String lng){
        String gcm_id = FirebaseInstanceId.getInstance().getToken();
        new ReusableAsync<>(this)
                .post("/token/google")
                .dialog("Logging...")
                .addData("user_name", userName)
                .addData("access_token", idToken)
                .addData("lat", lat)
                .addData("lng", lng)
                .addData("gcm_id", gcm_id)
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject data) throws JSONException {
                        String api_token = null;
                        api_token = data.getString("api_token");
                        boolean created = data.getBoolean("created");

                        // add api_token to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences(Global.USER_INFO_PREF_FILE, MODE_PRIVATE);
                        sharedPreferences.edit().putString(Global.PREF_USER_API_TOKEN, api_token).commit();

                        return null;
                    }
                })
                .onPost(new PostHandler() {
                    @Override
                    public void handle(Object data) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .send();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // show message if connection to Google failed
        if (connectionResult.equals(ConnectionResult.SERVICE_MISSING) ||
                connectionResult.equals(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) ||
                connectionResult.equals(ConnectionResult.SERVICE_DISABLED)) {
            Global.showMessage(this, "Google didn't successfully returned token",
                    Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
        }
    }

    private boolean isSignedOut() {
        Bundle extras = getIntent().getExtras();
        boolean isSignedOut;
        if (extras != null) {
            isSignedOut = true;
        } else {
            isSignedOut = false;
        }
        return isSignedOut;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
        if (isSignedOut() && i < 1) {
            logOutFromGoogleAccount();
            i++;
        }
    }

    private void logOutFromGoogleAccount() {
        if (googleApiClient.isConnected()) {
            googleApiClient.clearDefaultAccountAndReconnect();
            signOut();
            googleApiClient.connect();
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {

                SharedPreferences sharedPref = LoginTabbedActivity.this.getSharedPreferences(Global.USER_INFO_PREF_FILE,
                        Context.MODE_PRIVATE);

                // delete all info from the shared
                sharedPref.edit().clear().commit();

                // delete associated local database
                DatabaseUtilities databaseUtilities = new DatabaseUtilities();
                databaseUtilities.resetDatabase(LoginTabbedActivity.this);


            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            Log.d("location", "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
            if (sharedPreferences.edit().putFloat(Global.PREF_USER_LAT, (float) mLastLocation.getLatitude()).commit() &&
                    sharedPreferences.edit().putFloat(Global.PREF_USER_LNG, (float) mLastLocation.getLongitude()).commit()) {
            }
        }
    }

    protected void getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                //The application granted the permission
                getLocation();
            }

        }
    }

}