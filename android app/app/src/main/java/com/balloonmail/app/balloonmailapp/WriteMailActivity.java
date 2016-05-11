package com.balloonmail.app.balloonmailapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONObject;

import java.sql.SQLException;

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
        SentBalloon balloon = new SentBalloon(mailText.getText().toString(), 0, 0, 0);
        sentBalloonsDao.create(balloon);

        OpenHelperManager.releaseHelper();
    }
    private void spreadMail(EditText mailText){

        // get the mail text from the edit text
        String text = mailText.getText().toString();
        JSONObject sentJson = new JSONObject();
        //JsonObjectRequest sentMailJsonRequest = new JsonObjectRequest(Request.Method.POST, Global.SERVER_URL, )
        
    }
}
