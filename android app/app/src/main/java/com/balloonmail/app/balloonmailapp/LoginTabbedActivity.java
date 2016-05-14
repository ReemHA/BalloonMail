package com.balloonmail.app.balloonmailapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.DatabaseUtilities;
import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginTabbedActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private static final int RC_SIGN_IN = 9001;
    private static final String SIGN_IN_ERROR_TAG = "handle sign in";
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private GoogleApiClient googleApiClient;
    private int i = 0;
    DatabaseUtilities databaseUtilities;
    SharedPreferences sharedPreferences;
    private ProgressDialog mProgressDialog;
    public final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 0;
    private Location mLastLocation;
    private LocationRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tabbed);

        // get api_token from the shared preference
        sharedPreferences = this.getSharedPreferences(Global.USER_INFO_PREF_FILE, MODE_PRIVATE);
        String api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");

        databaseUtilities = new DatabaseUtilities();

        // get api_token from the shared preference
        // Log.d(LoginTabbedActivity.class.getSimpleName(), Global.USER_API_TOKEN);
        if (!isSignedOut() && api_token != "") {
            Intent intent = new Intent(LoginTabbedActivity.this, HomeActivity.class);
            startActivity(intent);
            LoginTabbedActivity.this.finish();
        }

        // create a new blank database for the new signed in user
        if (isSignedOut()) {
            databaseUtilities.createDatabase(LoginTabbedActivity.this);
        }
        // Configure sign in to request the user's id token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Global.SERVER_CLIENT_ID)
                .requestProfile()
                .requestEmail()
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
        if (Global.isConnected(this)) {

            // send an intent with the request to get the user's data
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {

            // show a toast if no network connection is available
            Toast.makeText(getApplicationContext(), NETWORK_CONNECTION_MSG, Toast.LENGTH_LONG).show();
        }
    }

    // handle the data returned from onActivityResult
    private void handleSignIntent(GoogleSignInResult result) throws JSONException {

        if (result.isSuccess()) {

            // get the idToken of the user
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();
            Log.d(LoginTabbedActivity.class.getSimpleName(), "id: " + idToken);


            // get the user name
            String userName = account.getDisplayName();
            Global.USER_NAME = userName;
            Log.d(LoginTabbedActivity.class.getSimpleName(), "name: " + Global.USER_NAME);
            sharedPreferences.edit().putString(Global.PREF_USER_NAME, userName).commit();

            // get the user email
            String userEmail = account.getEmail();
            Global.USER_EMAIL = userEmail;
            Log.d(LoginTabbedActivity.class.getSimpleName(), "email: " + Global.USER_EMAIL);
            sharedPreferences.edit().putString(Global.PREF_USER_EMAIL, userEmail).commit();

            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult succeeded");

            // send the idToken and username to the app server
            sendDataToServer(idToken, userName, userEmail);
        } else {
            Log.d(SIGN_IN_ERROR_TAG, "GoogleSignInResult failed");
        }
    }


    private void sendDataToServer(String idToken, final String userName, final String userEmail) {
        mProgressDialog = new ProgressDialog(LoginTabbedActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Logging...");
        mProgressDialog.show();
        new loginInfoToServer().execute(userName, idToken);
    }

    private class loginInfoToServer extends AsyncTask<String, Void, Void> {
        URL url;
        HttpURLConnection connection;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL(Global.SERVER_URL + "/token/google");
                connection = (HttpURLConnection) url.openConnection();

                // set connection to allow output
                connection.setDoOutput(true);

                // set connection to allow input
                connection.setDoInput(true);

                // set the request method to POST
                connection.setRequestMethod("POST");

                // set content-type property
                connection.setRequestProperty("Content-Type", "application/json");

                // set charset property to utf-8
                connection.setRequestProperty("charset", "utf-8");

                // put user name and id token in a JSONObject
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("user_name", strings[0]);
                jsonBody.put("access_token", strings[1]);
                jsonBody.put("lat", 0);
                jsonBody.put("lng", 0);
                jsonBody.put("gcm_id", 1);

                // connect to server
                connection.connect();

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                // write JSON body to the output stream
                outputStream.write(jsonBody.toString().getBytes("utf-8"));

                // flush to ensure all data in the stream is sent
                outputStream.flush();

                // close stream
                outputStream.close();

                // receive the response from server
                getResponseFromServer();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void getResponseFromServer() throws IOException, JSONException {
            // create StringBuilder object to append the input stream in
            StringBuilder sb = new StringBuilder();
            String line;

            // get input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // append stream in a the StringBuilder object
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();

            // convert StringBuilder object to string and store it in a variable
            String JSONResponse = sb.toString();
            Log.d(WriteMailActivity.class.getSimpleName(), JSONResponse);

            // convert response to JSONObject
            JSONObject response = new JSONObject(JSONResponse);

            // checks if an error is in the response
            if (!response.has("error")) {

                String api_token = response.getString("api_token");
                boolean created = response.getBoolean("created");
                // add api_token to SharedPreferences
                SharedPreferences sharedPreferences = LoginTabbedActivity.this.getSharedPreferences(Global.USER_INFO_PREF_FILE, MODE_PRIVATE);
                sharedPreferences.edit().putString(Global.PREF_USER_API_TOKEN, api_token).commit();


                Global.USER_API_TOKEN = api_token;
                Global.USER_IS_CREATED = created;

                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d("Response from Server: ", response.getString("error"));
                // Toast.makeText(getApplicationContext(), "Error signing in", Toast.LENGTH_LONG).show();
            }

            return;
        }
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

                //delete user info from Global
                Global.USER_API_TOKEN = null;
                Global.USER_EMAIL = null;
                Global.USER_NAME = null;
                Global.USER_IS_CREATED = false;

                SharedPreferences sharedPref = LoginTabbedActivity.this.getSharedPreferences(Global.USER_INFO_PREF_FILE,
                        Context.MODE_PRIVATE);

                if (sharedPref.edit().clear().commit()) {
                    Log.d(LoginTabbedActivity.class.getSimpleName(), "User info is removed from shared pref.");
                }

                // delete associated local database
                DatabaseUtilities databaseUtilities = new DatabaseUtilities();
                databaseUtilities.resetDatabase(LoginTabbedActivity.this);


                Log.d(Global.LOG_TAG, "signOut status:" + status.isSuccess());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();

    }


    //This method is invoked after requestLocationUpdates
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mLastLocation != null) {
            Log.d("location", "Latitude:" + mLastLocation.getLatitude() + ", Longitude:" + mLastLocation.getLongitude());
            if (sharedPreferences.edit().putFloat(Global.PREF_USER_LAT, (float) mLastLocation.getLatitude()).commit() &&
                    sharedPreferences.edit().putFloat(Global.PREF_USER_LNG, (float) mLastLocation.getLongitude()).commit()) {
                Log.d(LoginTabbedActivity.class.getSimpleName(), "LAT/LON is added");
            }
        }
    }

    //Create Location requests to periodically request a location update
    protected void createLocationRequest() {
        request = new LocationRequest();
        request.setInterval(20000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                //The application granted the permission
                startLocationUpdates();
            }

        }
    }

}