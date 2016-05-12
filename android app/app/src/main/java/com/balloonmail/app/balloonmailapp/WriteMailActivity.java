package com.balloonmail.app.balloonmailapp;

import android.content.Intent;
import android.app.ProgressDialog;
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

import com.balloonmail.app.balloonmailapp.Utilities.BalloonHolder;
import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.rest.RInterface;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRequest;

import java.sql.SQLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WriteMailActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    EditText mailText;
    Button spread;
    SharedPreferences sharedPreferences;
    private static final String CONFIRMATION_TO_SENT_MAIL = "Mail is successsfully sent to server.";
    private static final String FAILURE_TO_SENT_MAIL = "Mail is not sent to server.";

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
                sendBalloonToServer(mailText.getText().toString());
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

    private void sendBalloonToServer(String mailText) {
        SendBalloonRequest body = new SendBalloonRequest(mailText);
        Retrofit retrofit = Global.getRetrofit(this);
        RInterface rInterface = retrofit.create(RInterface.class);
        Call<SentBalloon> call = rInterface.postSentBalloon(body);
        final ProgressDialog mProgressDialog = new ProgressDialog(WriteMailActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Sending...");
        mProgressDialog.show();
        call.enqueue(new Callback<SentBalloon>() {
            @Override
            public void onResponse(Call<SentBalloon> call, Response<SentBalloon> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                SentBalloon balloon = response.body();
                Log.d(WriteMailActivity.class.getSimpleName(), response.body().toString());
                if (balloon.getError() == null) {
                    Log.d(WriteMailActivity.class.getSimpleName(), balloon.toString());
                    BalloonHolder balloonHolder = BalloonHolder.getInstance();
                    balloonHolder.setBalloon(balloon);
                    Toast.makeText(getApplicationContext(), "HEY", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d(WriteMailActivity.class.getSimpleName(), "Server error response:" + response.body().getError());
                }
            }

            @Override
            public void onFailure(Call<SentBalloon> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                if (t.getMessage() != null) {
                    Log.d("Error", t.getMessage());
                }
            }
        });
    }
}
