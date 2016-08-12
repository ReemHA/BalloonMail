package com.balloonmail.app.balloonmailapp.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.balloonmail.app.balloonmailapp.CardSent;
import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.activities.MailDetailsAndMapActivity;
import com.balloonmail.app.balloonmailapp.activities.MailsTabbedActivity;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    View rootView;
    Bundle savedInstanceState;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    ImageView emptyStateImage;
    private Context context;
    public SentMailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getContext();
        rootView = inflater.inflate(R.layout.fragment_sent_mails, container, false);
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_id);
        this.savedInstanceState = savedInstanceState;
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        sentBalloonList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSentBalloons();
            }
        });

        // Doa of SentBalloon table
        sentBalloonDao = null;
        try {
            sentBalloonDao = dbHelper.getSentBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        if (!Global.isConnected(context)) {
            try {
                cards = initCardsFromLocalDb();
                Collections.reverse(cards);
                mCardArrayAdapter.setCards(cards);
                Global.showMessage(context, "No internet conn",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            loadSentBalloons();
        }

        Log.d(SentMailsFragment.class.getSimpleName(), "1");

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
        Card card = new CardSent(balloon, getActivity());

        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                updateBalloonWithPaths(balloon);
                Global.balloonHolder.setBalloon((SentBalloon) balloon);
                Intent intent = new Intent(context, MailDetailsAndMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
    }

    private void loadSentBalloons() {
        new ReusableAsync<Integer>(context)
                .get("/balloons/sent")
                .bearer(Global.getApiToken(context))
                .progressBar(progressBar)
                .onSuccess(new SuccessHandler<Integer>() {
                    @Override
                    public Integer handle(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray("balloons");
                        cards = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            Date sent_at = null;
                            String sent_test = object.getString("sent_at");
                            sent_at = Global.getDateFromString(sent_test);
                            if (sent_at == null) {
                                throw new JSONException("error date format");
                            }
                            SentBalloon balloon = new SentBalloon(object.getString("text"), object.getInt("balloon_id"),
                                    object.getDouble("reach"), object.getInt("creeps"), object.getInt("refills"), object.getDouble("sentiment"),
                                    sent_at);
                            Card card = createCard(balloon);
                            cards.add(card);
                            balloonsMap.put(balloon, card);
                        }


                        return jsonArray.length();
                    }
                })
                .onPost(new PostHandler<Integer>() {
                    @Override
                    public void handle(Integer aVoid) {
                        if (swipeRefreshLayout.isRefreshing()) {
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
                })
                .send();
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

    private void updateBalloonWithPaths(final Balloon balloon) {
        new ReusableAsync<Void>(context)
                .get("/balloons/paths")
                .bearer(Global.getApiToken(context))
                .addQuery("balloon_id", Integer.toString(balloon.getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject jsonObject) throws JSONException {
                        HashMap<LatLng, ArrayList<LatLng>> paths = new HashMap<>();
                        double sourceIdJsonObject = jsonObject.getDouble("source");
                        JSONArray jsonArray = jsonObject.getJSONArray("paths");
                        LatLng userSourceLocation = new LatLng(0, 0);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            double from_user = object.getDouble("from_user");

                            LatLng source = new LatLng(object.getDouble("from_lat"), object.getDouble("from_lng"));

                            if (from_user == sourceIdJsonObject) {
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
                        return null;
                    }
                })
                .send();
    }

}
