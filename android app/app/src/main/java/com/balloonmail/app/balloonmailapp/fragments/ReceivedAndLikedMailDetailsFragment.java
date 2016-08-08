package com.balloonmail.app.balloonmailapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;

public class ReceivedAndLikedMailDetailsFragment extends Fragment {

    Balloon balloon;
    View rootView;

    public ReceivedAndLikedMailDetailsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView != null)
            return rootView;

        rootView = inflater.inflate(R.layout.fragment_received_and_liked_mail_details, container, false);

        balloon = Global.balloonHolder.getBalloon();


        Global.balloonHolder.setBalloon(null);
        
        return rootView;
    }

}
