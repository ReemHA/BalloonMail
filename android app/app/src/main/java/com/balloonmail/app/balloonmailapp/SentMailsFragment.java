package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
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
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class SentMailsFragment extends Fragment {

    private ArrayList<Card> cards;
    private LinearLayoutManager mLayoutManager;
    private HashMap<SentBalloon, Card> balloonsMap;
    private SentBalloonsListener mListener;
    private static List<SentBalloon> sentBalloonList;
    private DatabaseHelper dbHelper;
    private Dao<SentBalloon, Integer> sentBalloonDao;
    private DateFormat dateFormat;
    private ProgressDialog mProgressDialog;
    View rootView;
    Bundle savedInstanceState;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    SharedPreferences sharedPreferences;

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
        sharedPreferences = getContext().getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        sentBalloonList = new ArrayList<>();


        // Doa of SentBalloon table
        sentBalloonDao = null;
        try {
            sentBalloonDao = dbHelper.getSentBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        if (Global.isConnected(getContext())) {
            try {
                loadSentBalloons();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else
            try {
                cards = initCardsFromLocalDb();
                mCardArrayAdapter.setCards(cards);
            } catch (SQLException e) {
                e.printStackTrace();
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

        ImageView image = (ImageView) rootView.findViewById(R.id.emptyStateImage);
        if(cards.size() == 0){
            image.setBackgroundResource(R.drawable.empty_state);
        }else{
            image.setBackgroundResource(0);
        }

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

    private Card createCard(Balloon balloon) {
        Card card = new CardSent(balloon, getActivity().getBaseContext());

        final Balloon balloon1 = balloon;
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getContext(), MailDetailsAndMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = new Bundle();
                bundle.putSerializable("balloon", balloon1);
                intent.putExtras(bundle);
                getContext().startActivity(intent);
            }
        });

        CardExpand cardExpand = new CustomSentExpandCard(balloon, getActivity().getBaseContext(), savedInstanceState);
        card.addCardExpand(cardExpand);
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

    interface SentBalloonsListener {
        void getSentBalloons(List<SentBalloon> balloonList);
    }

    public void setListener(SentBalloonsListener listener) {
        mListener = listener;
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

    private void loadSentBalloons() throws ExecutionException, InterruptedException {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Getting your updated balloons..");
        mProgressDialog.show();
        new fetchSentBalloonsFromServer().execute();
    }

    private class fetchSentBalloonsFromServer extends AsyncTask<Object, Void, Void> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected Void doInBackground(Object... params) {
            try {
                url = new URL(Global.SERVER_URL + "/balloons/sent");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer "+sharedPreferences.getString(Global.PREF_USER_API_TOKEN, ""));
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
                SentBalloon balloon = new SentBalloon(object.getString("text"), object.getInt("balloon_id"),
                        object.getDouble("reach"), object.getInt("creeps"), object.getInt("refills"), object.getDouble("sentiment"),
                        dateFormat.parse(object.getString("sent_at")));
                Card card = createCard(balloon);
                cards.add(card);
                balloonsMap.put(balloon, card);
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
