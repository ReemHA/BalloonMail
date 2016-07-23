package com.balloonmail.app.balloonmailapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.activities.MailsTabbedActivity;
import com.balloonmail.app.balloonmailapp.utilities.Global;

public class HomeActivityFragment extends Fragment {

    public HomeActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        ImageButton sentButton = (ImageButton)rootView.findViewById(R.id.sentImageButton);
        sentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MailsTabbedActivity.class);
                intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.SENT_TABBED_PAGE);
                getActivity().finish();
                startActivity(intent);
            }
        });

        ImageButton receivedButton = (ImageButton)rootView.findViewById(R.id.receivedImageButton);
        receivedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MailsTabbedActivity.class);
                intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.RECEIVED_TABBED_PAGE);
                getActivity().finish();
                startActivity(intent);
            }
        });

        ImageButton likedButton = (ImageButton)rootView.findViewById(R.id.likedImageButton);
        likedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MailsTabbedActivity.class);
                intent.putExtra(Global.ARG_MAILS_TABBED_TAG, Global.LIKES_TABBED_PAGE);
                getActivity().finish();
                startActivity(intent);
            }
        });

        return rootView;
    }
}