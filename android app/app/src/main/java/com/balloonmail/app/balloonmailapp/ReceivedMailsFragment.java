package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
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

        // Doa of Received table
        receivedBalloonDao = null;
        try {
            receivedBalloonDao = dbHelper.getReceivedBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        if (Global.isConnected(getContext())) {
            try {
                loadReceivedBalloons();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
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

    private Card createCard(Balloon balloon){
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


    private void loadReceivedBalloons() throws ExecutionException, InterruptedException {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Getting your received balloons..");
        mProgressDialog.show();
        new fetchReceivedBalloonsFromServer().execute();
    }

    private class fetchReceivedBalloonsFromServer extends AsyncTask<Object, Void, Void> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected Void doInBackground(Object... params) {
            try {
                url = new URL(Global.SERVER_URL + "/balloons/received");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer "+Global.USER_API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                getResponse();

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

        private void getResponse() throws IOException, JSONException, ParseException {
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            streamReader.close();
            if (mProgressDialog.isShowing())
                mProgressDialog.dismiss();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("balloons");
            cards = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ReceivedBalloon balloon = new ReceivedBalloon(object.getString("text"), object.getInt("balloon_id"),
                        object.getDouble("sentiment"),
                        dateFormat.parse(object.getString("sent_at")));
                Card card = createCard(balloon);
                cards.add(card);
                balloonsMap.put(balloon, card);
            }
            //empty state
            if(jsonArray.length() == 0){
                emptyStateImage.setBackgroundResource(R.drawable.empty_state);
            }else{
                emptyStateImage.setBackgroundResource(0);
            }
            return;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mCardArrayAdapter.setCards(cards);
            mCardArrayAdapter.notifyDataSetChanged();
        }
    }

}
