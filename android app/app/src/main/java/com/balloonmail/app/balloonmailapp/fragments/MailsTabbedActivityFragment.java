package com.balloonmail.app.balloonmailapp.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmailapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MailsTabbedActivityFragment extends Fragment {

    public MailsTabbedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mails_tabbed, container, false);
    }
}
