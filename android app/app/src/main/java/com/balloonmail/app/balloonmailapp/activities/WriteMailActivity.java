package com.balloonmail.app.balloonmailapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;

import org.json.JSONException;
import org.json.JSONObject;

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
        new ReusableAsync<Void>(this)
                .post("/balloons/create")
                .dialog("Sending...")
                .bearer(Global.getApiToken(this))
                .addData("text", text)
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject response) throws JSONException {
                        // get balloon attributes from the response
                        SentBalloon balloon = null;
                        try {
                            balloon = new SentBalloon(response.getString("text"), response.getInt("balloon_id"), response.getDouble("reach"),
                                    response.getInt("creeps"), response.getInt("refills"), response.getDouble("sentiment"),
                                    dateFormat.parse(response.getString("sent_at")));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Global.balloonHolder.setBalloon(balloon);
                        return null;
                    }
                })
                .onPost(new PostHandler() {
                    @Override
                    public void handle(Object data) {
                        // move from this activity
                        Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.SENT_TABBED_PAGE);
                        startActivity(intent);
                        finish();
                    }
                })
                .send();
    }
}
