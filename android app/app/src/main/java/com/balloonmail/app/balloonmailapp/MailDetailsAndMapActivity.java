package com.balloonmail.app.balloonmailapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.balloonmail.app.balloonmailapp.models.Balloon;

public class MailDetailsAndMapActivity extends AppCompatActivity {

    Balloon balloon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_details_and_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null){

            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            balloon = (Balloon) bundle.getSerializable("balloon");


            Bundle arguments = new Bundle();
            arguments.putSerializable("balloonDetailFragment", balloon);
            MailDetailsAndMapActivityFragment mailDetailsAndMapActivityFragment = new MailDetailsAndMapActivityFragment();
            mailDetailsAndMapActivityFragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, mailDetailsAndMapActivityFragment)
                    .commit();
        }
    }


}
