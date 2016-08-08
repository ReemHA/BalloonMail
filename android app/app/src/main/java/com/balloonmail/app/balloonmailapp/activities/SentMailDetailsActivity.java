package com.balloonmail.app.balloonmailapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.balloonmail.app.balloonmailapp.R;

public class SentMailDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_mail_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*if(savedInstanceState == null){

            //Intent intent = this.getIntent();
            //Bundle bundle = intent.getExtras();
            //balloon = (Balloon) bundle.getSerializable("balloon");


            //Bundle arguments = new Bundle();
            //arguments.putSerializable("balloonDetailFragment", balloon);
            //SentMailDetailsFragment mailDetailsAndMapActivityFragment = new SentMailDetailsFragment();
            //mailDetailsAndMapActivityFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new SentMailDetailsFragment())
                    .commit();
        }*/
    }


}
