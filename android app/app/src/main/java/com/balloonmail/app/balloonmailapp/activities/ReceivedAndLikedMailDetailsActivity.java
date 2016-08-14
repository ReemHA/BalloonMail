package com.balloonmail.app.balloonmailapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.utilities.Global;

public class ReceivedAndLikedMailDetailsActivity extends AppCompatActivity {
    String receivedOrLiked; //"r" or "l"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_and_liked_mail_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        receivedOrLiked = getIntent().getExtras().getString(Global.RECEIVED_OR_LIKED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            backIntentToReceivedOrLiked(receivedOrLiked);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backIntentToReceivedOrLiked(receivedOrLiked);
    }

    public void backIntentToReceivedOrLiked(String receivedOrLiked){
        Intent intent = new Intent(getApplicationContext(), MailsTabbedActivity.class);
        if(receivedOrLiked.equals("r")){
            Log.d("Intents", "MailDetails back to received");
            intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.RECEIVED_TABBED_PAGE);
        }else if(receivedOrLiked.equals("l")){
            Log.d("Intents", "MailDetails back to liked");
            intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.LIKES_TABBED_PAGE);
        }
        finish();
    }
}
