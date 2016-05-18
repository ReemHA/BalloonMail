package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;


public class ReceivedMailsFragment extends Fragment {
    private ArrayList<Card> cards;
    private LinearLayoutManager mLayoutManager;
    private HashMap<ReceivedBalloon, Card> balloonsMap;
    private static List<ReceivedBalloon> receivedBalloonList;
    private DatabaseHelper dbHelper;
    private Dao<ReceivedBalloon, Integer> receivedBalloonDao;
    private DateFormat dateFormat;
    private ProgressDialog mProgressDialog;
    View rootView;
    Bundle savedInstanceState;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    private SharedPreferences sharedPreferences;
    private static String api_token;
    ImageView emptyStateImage;
    public ReceivedMailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_received_mails, container, false);
        this.savedInstanceState = savedInstanceState;
        dbHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        receivedBalloonList = new ArrayList<>();

        sharedPreferences = getContext().getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);
        api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");

        // Doa of Received table
        receivedBalloonDao = null;
        try {
            receivedBalloonDao = dbHelper.getReceivedBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        if (!Global.isConnected(getContext())) {
            try {
                cards = initCardsFromLocalDb();
                mCardArrayAdapter.setCards(cards);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvReceivedCardRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

        emptyStateImage = (ImageView) rootView.findViewById(R.id.emptyStateImage);

        return rootView;
    }

    public ArrayList<Card> initCardsFromLocalDb() throws SQLException {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;

        // a received balloon in Db?
        List<ReceivedBalloon> receivedBalloonsListInDb = receivedBalloonDao.queryForAll();
        if (receivedBalloonsListInDb.size() > 0 && receivedBalloonsListInDb != null) {
            for (int i = 0; i < receivedBalloonsListInDb.size(); i++) {
                card = createCard(receivedBalloonsListInDb.get(i));
                cards.add(card);
            }
        }

        return cards;
    }

    private Card createCard(Balloon balloon) {
        Card card = new CardReceived(balloon, getActivity().getBaseContext(), savedInstanceState);
        card.setCardElevation(getResources().getDimension(R.dimen.card_shadow_elevation));
        return card;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(MailsTabbedActivity.class.getSimpleName(), "onPause saving");
        receivedBalloonList.addAll(balloonsMap.keySet());
        saveReceivedBalloonsToDatabase(receivedBalloonList);
    }

    private void saveReceivedBalloonsToDatabase(List<ReceivedBalloon> balloonList) {

        if (balloonList.size() > 0 && balloonList != null) {

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    receivedBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof Activity){
//            mActivity = (Activity) context;
//        }
//
//    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && Global.isConnected(getContext())){
            try {
                loadReceivedBalloons();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadReceivedBalloons() throws ExecutionException, InterruptedException {
        new fetchReceivedBalloonsFromServer().execute();
    }

    private class fetchReceivedBalloonsFromServer extends AsyncTask<Object, Void, Integer> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Getting your received balloons..");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                url = new URL(Global.SERVER_URL + "/balloons/received");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer " + api_token);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                return getResponse();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        private int getResponse() throws IOException, JSONException, ParseException {
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            streamReader.close();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("balloons");
            cards = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ReceivedBalloon balloon = new ReceivedBalloon(object.getString("text"), object.getInt("balloon_id"),
                        object.getDouble("sentiment"), object.getDouble("lat"), object.getDouble("lng"),
                        dateFormat.parse(object.getString("sent_at")));
                Card card = createCard(balloon);
                cards.add(card);
                balloonsMap.put(balloon, card);
            }

            return jsonArray.length();
        }

        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            if (aVoid != null) {
                if (aVoid == 0) {
                    emptyStateImage.setBackgroundResource(R.drawable.empty_state);
                } else {
                    emptyStateImage.setBackgroundResource(0);
                }
            }
            mCardArrayAdapter.setCards(cards);
            mCardArrayAdapter.notifyDataSetChanged();

        }
    }

}
