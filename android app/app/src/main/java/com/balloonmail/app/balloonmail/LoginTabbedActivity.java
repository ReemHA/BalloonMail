package com.balloonmail.app.balloonmail;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.balloonmail.app.balloonmail.Utilities.Global;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoginTabbedActivity extends AppCompatActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final int RC_SIGN_IN = 9001;
    private static final String SIGN_IN_ERROR_TYPE = "Sign in";
    private static final String SERVER_RESPONSE_ERROR_TYPE = "Server response:";
    private static final String NETWORK_CONNECTION_MSG = "Please check your network connection.";
    private static final String ERROR_LOGGING_IN_MSG = "Error logging in. Please make sure you have a google account.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_tabbed);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //setupTabIcons()

    }
    /*private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }*/

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment(), "Login");
        adapter.addFragment(new SignUpFragment(), "Sign up");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // upon returning after the sign in call check result
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // store returned data
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                // handle returned data
                handleSignIntent(result);
            }
        }
    }

    private void handleSignIntent(GoogleSignInResult result) {
        if (result.isSuccess()) {

            // fetch idToken of the user
            GoogleSignInAccount account = result.getSignInAccount();
            String idToken = account.getIdToken();

            // get the username
            String userName = account.getDisplayName();

            Log.d(SIGN_IN_ERROR_TYPE, "GoogleSignInResult succeeded");

            // send the idToken to the server
            sendDataToServer(idToken, userName);
        } else {
            Log.d(SIGN_IN_ERROR_TYPE, "GoogleSignInResult failed");
        }
    }

    private void sendDataToServer(String idToken, String userName) {
        new sendJSONDataToServer().execute(idToken,userName);
    }

    class sendJSONDataToServer extends AsyncTask<String, String, String> {
        URL url;
        HttpURLConnection connection;

        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(Global.SERVER_URL+"/token/google");
                connection = (HttpURLConnection) url.openConnection();

                // set connection to allow output
                connection.setDoOutput(true);

                // set connection to allow input
                connection.setDoInput(true);

                // set the request method to POST
                connection.setRequestMethod("POST");

                // set content-type property
                connection.setRequestProperty("Content-Type", "application/json");

                // set charset property to utf-8
                connection.setRequestProperty("charset", "utf-8");


                // set accept property
                connection.setRequestProperty("Accept", "application/json");

                // put user name and id token in a JSONObject
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("access_token", strings[0]);
                jsonBody.put("user_name", strings[1]);

                // connect to server
                connection.connect();

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());

                // write JSON body to the output stream
                outputStream.write(jsonBody.toString().getBytes("utf-8"));

                // flush to ensure all data in the stream is sent
                outputStream.flush();

                // close stream
                outputStream.close();

                // receive the response from server
                getResponseFromServer();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }

        private void getResponseFromServer() throws IOException, JSONException {
            // create StringBuilder object to append the input stream in
            StringBuilder sb = new StringBuilder();
            String line;

            // get input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // append stream in a the StringBuilder object
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            reader.close();

            // convert StringBuilder object to string and store it in a variable
            String JSONResponse = sb.toString();
            Log.d(SERVER_RESPONSE_ERROR_TYPE, JSONResponse);

            // convert response to JSONObject
            JSONObject response = new JSONObject(JSONResponse);
            response = response.getJSONObject("result");

            // checks if an error is in the response
            if (!response.has("error")) {
                String userID, api_token;

                // get api_token of the user from the response
                api_token = response.getString("api_token");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

                // checks if he is a new user
                if (response.getBoolean("created")) {
                    // insert a new user record
                    //insertNewUserData(userID, api_token);

                } else {
                    // save api_token in the record of the user of this userID
                    //insertApiTokenToUser(userID, api_token);
                }
            } else {
                Log.d("Response from Server: ", response.getString("error"));
            }

            return;
        }
    }
}