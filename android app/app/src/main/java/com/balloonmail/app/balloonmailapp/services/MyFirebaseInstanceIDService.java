package com.balloonmail.app.balloonmailapp.services;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.balloonmail.app.balloonmailapp.activities.WriteMailActivity;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Reem Hamdy on 7/13/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        // get api_token from the shared preference
        SharedPreferences sharedPreferences = this.getSharedPreferences(Global.USER_INFO_PREF_FILE, MODE_PRIVATE);
        String api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        if (api_token != "") {
            sendRegistrationToServer(refreshedToken);
        }
    }

    private void sendRegistrationToServer(String token) {
        new RegistrationTokenToServer().execute(token);
    }

    private class RegistrationTokenToServer extends AsyncTask<String, Void, Void> {
        URL url;
        HttpURLConnection connection;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL(Global.SERVER_URL + "/refresh_token");
                connection = (HttpURLConnection) url.openConnection();

                // set connection to allow output
                connection.setDoOutput(true);

                // set the request method to POST
                connection.setRequestMethod("POST");


                // set content-type property
                connection.setRequestProperty("Content-Type", "application/json");

                // set charset property to utf-8
                connection.setRequestProperty("charset", "utf-8");

                // put user name and id token in a JSONObject
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("gcm_id", strings[0]);

                // connect to server
                connection.connect();

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                // write JSON body to the output stream
                outputStream.write(jsonBody.toString().getBytes("utf-8"));

                // flush to ensure all data in the stream is sent
                outputStream.flush();

                // close stream
                outputStream.close();

                getResponse();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }


        private void getResponse() throws IOException, JSONException {
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
            if (response.has("error")) {
                final AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Can't send FCM id");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();

                    }
                });
            }
        }
    }
}