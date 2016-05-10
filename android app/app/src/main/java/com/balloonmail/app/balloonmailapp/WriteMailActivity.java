package com.balloonmail.app.balloonmailapp;

import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.rest.RInterface;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRequest;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonResponse;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONObject;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteMailActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText mailText;
    Button spread;

    //TextWatcher
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            enableOrDisableEditText();
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };
    private void enableOrDisableEditText(){
        if(isEmpty(mailText)){
            spread.setEnabled(false);
        }else{
            spread.setEnabled(true);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);
        mailText = (EditText) findViewById(R.id.mail_text);
        String mailTextValue = mailText.getText().toString();
        spread = (Button) findViewById(R.id.spread);

        enableOrDisableEditText();
        mailText.addTextChangedListener(textWatcher);
        spread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    writeMail(mailText);
                    moveToIntent();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
    private void moveToIntent(){
        Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
        startActivity(intent);
        finish();
    }
    private void writeMail(EditText mailText) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        Dao<SentBalloon, Integer> sentBalloonsDao = dbHelper.getSentBalloonDao();

        // create a new balloon
        sentBalloonsDao.create(new SentBalloon(mailText.getText().toString()));

        OpenHelperManager.releaseHelper();
    }
    private void spreadMail(EditText mailText){

        //sendBalloonToServer(mailText, user_email);


    }
    private void sendBalloonToServer(String mailText, String userEmail){
        SendBalloonRequest body = new SendBalloonRequest(mailText, userEmail);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RInterface rInterface = retrofit.create(RInterface.class);
        Call<SendBalloonResponse> call = rInterface.postMail(body);
        call.enqueue(new Callback<SendBalloonResponse>() {
            @Override
            public void onResponse(Call<SendBalloonResponse> call, Response<SendBalloonResponse> response) {
                if (response.body().getResponse().equals("true")){
                    Toast.makeText(getApplicationContext(), CONFIRMATION_TO_SENT_MAIL, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SendBalloonResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(),FAILURE_TO_SENT_MAIL, Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    Log.d("Error", t.getMessage());
                }
            }
        });
    }
}
