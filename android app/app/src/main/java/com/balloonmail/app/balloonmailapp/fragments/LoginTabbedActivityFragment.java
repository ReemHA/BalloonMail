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
public class LoginTabbedActivityFragment extends Fragment {

    public LoginTabbedActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_tabbed, container, false);
    }
}
