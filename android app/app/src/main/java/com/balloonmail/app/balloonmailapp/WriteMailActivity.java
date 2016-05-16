package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WriteMailActivity extends AppCompatActivity {

    EditText mailText;
    Button spread;
    private static final String CONFIRMATION_TO_SENT_MAIL = "Mail is successsfully sent to server.";
    private static final String FAILURE_TO_SENT_MAIL = "Mail is not sent to server.";
    private DateFormat dateFormat;
    private ProgressDialog mProgressDialog;
    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            enableOrDisableEditText();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private void enableOrDisableEditText() {
        if (isEmpty(mailText)) {
            spread.setEnabled(false);
        } else {
            spread.setEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);
        mailText = (TextInputEditText) findViewById(R.id.mail_text);
        spread = (Button) findViewById(R.id.spread);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);

        enableOrDisableEditText();
        mailText.addTextChangedListener(textWatcher);
        spread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mailText.getText().toString();
                sendBalloonToServer(text);

            }
        });
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private void sendBalloonToServer(String text) {
        mProgressDialog = new ProgressDialog(WriteMailActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Sending...");
        mProgressDialog.show();
        new SendingBalloonsToServer().execute(text);
    }

    class SendingBalloonsToServer extends AsyncTask<String, Void, Void> {
        URL url;
        HttpURLConnection connection;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Global.USER_INFO_PREF_FILE,
                getApplicationContext().MODE_PRIVATE);

        String api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");


        @Override
        protected Void doInBackground(String... strings) {
            try {
                url = new URL(Global.SERVER_URL + "/balloons/create");
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

                connection.setRequestProperty("authorization", "Bearer " + api_token);
                // set accept property
                connection.setRequestProperty("Accept", "application/json");

                // put user name and id token in a JSONObject
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("text", strings[0]);


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

                // get balloon attributes from the response
                SentBalloon balloon = null;
                try {
                    balloon = new SentBalloon(response.getString("text"), response.getInt("balloon_id"), response.getDouble("reach"),
                            response.getInt("creeps"), response.getInt("refills"), response.getDouble("sentiment"), dateFormat.parse(response.getString("sent_at")));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Global.balloonHolder.setBalloon(balloon);

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                // move from this activity
                Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
                startActivity(intent);
                finish();
            } else {
            }

            return;
        }
    }
}
