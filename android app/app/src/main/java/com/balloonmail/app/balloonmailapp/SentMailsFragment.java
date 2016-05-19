package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.google.android.gms.maps.model.LatLng;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class SentMailsFragment extends Fragment {

    private ArrayList<Card> cards;
    private LinearLayoutManager mLayoutManager;
    private HashMap<SentBalloon, Card> balloonsMap;
    private static List<SentBalloon> sentBalloonList;
    private DatabaseHelper dbHelper;
    private Dao<SentBalloon, Integer> sentBalloonDao;
    private DateFormat dateFormat;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView;
    Bundle savedInstanceState;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    ImageView emptyStateImage;
    private SharedPreferences sharedPreferences;
    private static String api_token;

    public SentMailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sent_mails, container, false);
        dbHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        this.savedInstanceState = savedInstanceState;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        sentBalloonList = new ArrayList<>();

        progressBar = (ProgressBar) rootView.findViewById(R.id.sentProgressBar);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    loadSentBalloons();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        sharedPreferences = getContext().getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);
        api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");
        // Doa of SentBalloon table
        sentBalloonDao = null;
        try {
            sentBalloonDao = dbHelper.getSentBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        if (!Global.isConnected(getContext())) {
            try {
                cards = initCardsFromLocalDb();
                Collections.reverse(cards);
                mCardArrayAdapter.setCards(cards);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvCardRecyclerView);
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

        // a sent balloon in Db?
        List<SentBalloon> sentBalloonListInDb = sentBalloonDao.queryForAll();
        if (sentBalloonListInDb.size() > 0 && sentBalloonListInDb != null) {
            for (int i = 0; i < sentBalloonListInDb.size(); i++) {
                card = createCard(sentBalloonListInDb.get(i));
                cards.add(card);
            }
        }

        // a sent balloon in holder?
        SentBalloon balloon = Global.balloonHolder.getBalloon();
        if (balloon != null) {
            card = createCard(balloon);
            cards.add(card);

            // to hash map
            balloonsMap.put(balloon, card);
        }

        //reset balloon object in holder
        Global.balloonHolder.setBalloon(null);
        return cards;
    }

    private Card createCard(final Balloon balloon) {
        Card card = new CardSent(balloon, getActivity().getBaseContext());

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                updateBalloonWithPaths(balloon);
                Global.balloonHolder.setBalloon((SentBalloon) balloon);
                Intent intent = new Intent(getContext(), MailDetailsAndMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //Bundle bundle = new Bundle();
                //bundle.putSerializable("balloon", balloon1);
                //intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });

        card.setId(balloon.getSent_at().toString());
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
        sentBalloonList.addAll(balloonsMap.keySet());
        saveSentBalloonsToDatabase(sentBalloonList);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && Global.isConnected(getContext())) {
            try {
                loadSentBalloons();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void loadSentBalloons() throws ExecutionException, InterruptedException {
        new fetchSentBalloonsFromServer().execute();
    }


    private void saveSentBalloonsToDatabase(List<SentBalloon> balloonList) {

        if (balloonList.size() > 0 && balloonList != null) {

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    sentBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();
        }
    }

    private class fetchSentBalloonsFromServer extends AsyncTask<Object, Void, Integer> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            try {
                url = new URL(Global.SERVER_URL + "/balloons/sent");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                sharedPreferences = getContext().getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);
                api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");
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
                SentBalloon balloon = new SentBalloon(object.getString("text"), object.getInt("balloon_id"),
                        object.getDouble("reach"), object.getInt("creeps"), object.getInt("refills"), object.getDouble("sentiment"),
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
            if(progressBar.isShown()){
                progressBar.setVisibility(View.GONE);
            }
            if(swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (aVoid != null) {
                if (aVoid == 0) {
                    emptyStateImage.setBackgroundResource(R.drawable.empty_state);
                } else {
                    emptyStateImage.setBackgroundResource(0);
                }
            }
            Collections.reverse(cards);
            mCardArrayAdapter.setCards(cards);
            mCardArrayAdapter.notifyDataSetChanged();
        }
    }


    private void updateBalloonWithPaths(Balloon balloon){
        new GetAllPathsOfSource().execute(balloon);
    }

    private class GetAllPathsOfSource extends AsyncTask<Balloon, Void, Void> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected Void doInBackground(Balloon... balloons) {

            try {
                url = new URL(Global.SERVER_URL + "/balloons/paths" + "?balloon_id=" + balloons[0].getBalloon_id());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer " + api_token);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                getResponse(balloons[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return null;
        }

        private void getResponse(Balloon balloon) throws IOException, JSONException, ParseException {
            HashMap<LatLng, ArrayList<LatLng>> paths = new HashMap<>();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            streamReader.close();

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            double sourceIdJsonObject = jsonObject.getDouble("source");
            JSONArray jsonArray = jsonObject.getJSONArray("paths");
            LatLng userSourceLocation = new LatLng(0,0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                double from_user = object.getDouble("from_user");

                LatLng source = new LatLng(object.getDouble("from_lat"), object.getDouble("from_lng"));

                if(from_user == sourceIdJsonObject){
                    userSourceLocation = source;
                }

                ArrayList<LatLng> dests = paths.get(source);
                if (!paths.containsKey(source)) {
                    dests = new ArrayList<>();
                    paths.put(source, dests);
                }
                dests.add(new LatLng(object.getDouble("to_lat"), object.getDouble("to_lng")));
            }
            balloon.setDestinationsHashMap(paths);
            balloon.setSourceBalloon(userSourceLocation.latitude, userSourceLocation.longitude);
            Global.balloonHolder.setBalloon((SentBalloon) balloon);

            return;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
