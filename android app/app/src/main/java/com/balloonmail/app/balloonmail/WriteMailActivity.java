package com.balloonmail.app.balloonmail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

public class WriteMailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_mail);
        final EditText mailText = (EditText) findViewById(R.id.mail_text);
        Button spread = (Button) findViewById(R.id.spread);
        spread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spreadMail(mailText);
            }
        });
    }

    private void spreadMail(EditText mailText){

        // get the mail text from the edit text
        String text = mailText.getText().toString();
        JSONObject sentJson = new JSONObject();
        //JsonObjectRequest sentMailJsonRequest = new JsonObjectRequest(Request.Method.POST, Global.SERVER_URL, )
        
    }
}
