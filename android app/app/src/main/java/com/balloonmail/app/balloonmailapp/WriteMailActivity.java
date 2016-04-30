package com.balloonmail.app.balloonmailapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloons;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONObject;

import java.sql.SQLException;

public class WriteMailActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);
        final EditText mailText = (EditText) findViewById(R.id.mail_text);
        Button spread = (Button) findViewById(R.id.spread);
        spread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    writeMail(mailText);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void writeMail(EditText mailText) throws SQLException {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        Dao<SentBalloons, Integer> sentBalloonsDao = dbHelper.getSentBalloonDao();

        // create a new balloon
        sentBalloonsDao.create(new SentBalloons(mailText.getText().toString()));

        OpenHelperManager.releaseHelper();
    }
    private void spreadMail(EditText mailText){

        // get the mail text from the edit text
        String text = mailText.getText().toString();
        JSONObject sentJson = new JSONObject();
        //JsonObjectRequest sentMailJsonRequest = new JsonObjectRequest(Request.Method.POST, Global.SERVER_URL, )
        
    }
}
