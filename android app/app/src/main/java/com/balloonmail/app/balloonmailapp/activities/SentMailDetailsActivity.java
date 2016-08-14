package com.balloonmail.app.balloonmailapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.utilities.Global;

public class SentMailDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_mail_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backIntentToSent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backIntentToSent();
    }

    public void backIntentToSent(){
        Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
        intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.SENT_TABBED_PAGE);
        finish();
    }
}
