package com.balloonmail.app.balloonmailapp;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class LoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String SIGN_IN_ERROR_TYPE = "Sign in";
    private static final String SERVER_RESPONSE_ERROR_TYPE = "Server response:";
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private static final String ERROR_LOGGING_IN_MSG = "Error logging in. Please make sure you have a google account.";
    private static final String SERVER_URL = "http://192.168.43.122:3000";

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Configure sign in to request the user's id token
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(Global.SERVER_CLIENT_ID)
                .requestProfile()
                .build();

        // GoogleApiClient is main entry for Google Play services integration. Build GoogleApiClient to access the options specified by gso
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton google_sign_in = (SignInButton) rootView.findViewById(R.id.login_google_button);
        google_sign_in.setSize(SignInButton.SIZE_WIDE);
        google_sign_in.setScopes(gso.getScopeArray());
        google_sign_in.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.login_google_button:
                        // attempts signing in
                        signIn();
                        break;
                }
            }
        });
        return rootView;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void signIn() {
        // check whether a network connection is available or not
        if (checkNetworkConnection()) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            Toast.makeText(getContext(), NETWORK_CONNECTION_MSG, Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkNetworkConnection() {
        // check the state of network connectivity
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // get an instance of the current active network
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        }
        return false;
    }

}
