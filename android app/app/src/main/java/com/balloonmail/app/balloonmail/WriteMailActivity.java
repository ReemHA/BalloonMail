package com.balloonmail.app.balloonmail;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
        String text = mailText.getText().toString();
        
    }
}
