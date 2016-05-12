package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.BalloonHolder;
import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.rest.RInterface;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRequest;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRespond;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WriteMailActivity extends AppCompatActivity {

    EditText mailText;
    Button spread;
    EditText editText;
    private static final String CONFIRMATION_TO_SENT_MAIL = "Mail is successsfully sent to server.";
    private static final String FAILURE_TO_SENT_MAIL = "Mail is not sent to server.";

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

    private void moveToIntent() {
        Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
        startActivity(intent);
        finish();
    }


    class SendingBalloonsToServer extends AsyncTask<>

    /*private void sendBalloonToServer(String mailText) {
        SendBalloonRequest body = new SendBalloonRequest(mailText);
        Retrofit retrofit = Global.getRetrofit(this);
        RInterface rInterface = retrofit.create(RInterface.class);
        Call<SendBalloonRespond> call = rInterface.postSentBalloon(body);
        final ProgressDialog mProgressDialog = new ProgressDialog(WriteMailActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Sending...");
        mProgressDialog.show();
        call.enqueue(new Callback<SendBalloonRespond>() {
                         @Override
                         public void onResponse(Call<SendBalloonRespond> call, Response<SendBalloonRespond> response) {
                             if (response.isSuccessful()) {
                                 Log.d(WriteMailActivity.class.getSimpleName(), response.body().toString());
                                 if (mProgressDialog.isShowing())
                                     mProgressDialog.dismiss();
                                 SendBalloonRespond balloon = response.body();
                                 if (balloon.getError() == null) {
                                     Log.d(WriteMailActivity.class.getSimpleName(), balloon.toString());
                                     BalloonHolder balloonHolder = BalloonHolder.getInstance();
                                     SentBalloon sentBalloon = new SentBalloon(balloon.getText(),
                                             balloon.getBalloon_id(), balloon.getReach(), balloon.getCreeps(), balloon.getRefills(),
                                             balloon.getSentiment(), balloon.getSent_at());
                                     balloonHolder.setBalloon(sentBalloon);
                                     Toast.makeText(getApplicationContext(), CONFIRMATION_TO_SENT_MAIL, Toast.LENGTH_SHORT).show();
                                     moveToIntent();
                                 } else {
                                     Log.d(WriteMailActivity.class.getSimpleName(), "Server error response:" + response.body().getError());
                                 }
                             }
                         }

                         @Override
                         public void onFailure(Call<SendBalloonRespond> call, Throwable t) {
                             if (mProgressDialog.isShowing())
                                 mProgressDialog.dismiss();
                             if (t.getMessage() != null) {
                                 Log.d("Error", t.getMessage());
                             }
                         }
                     }

        );
    }*/
}
