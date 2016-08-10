package com.balloonmail.app.balloonmailapp.fragments;

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

import com.balloonmail.app.balloonmailapp.CardLikes;
import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class LikedMailsFragment extends Fragment {

    private ArrayList<Card> cards;
    private HashMap<LikedBalloon, Card> balloonsMap;
    private static List<LikedBalloon> likedBalloonList;
    private DatabaseHelper dbHelper;
    private Dao<LikedBalloon, Integer> likedBalloonDao;
    private DateFormat dateFormat;
    View rootView;
    Bundle savedInstanceState;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    ImageView emptyStateImage;


    public LikedMailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_liked_mails, container, false);

        this.savedInstanceState = savedInstanceState;
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_id);

        dbHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        likedBalloonList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadLikedBalloons();
            }
        });

        // Doa of Liked table
        likedBalloonDao = null;
        try {
            likedBalloonDao = dbHelper.getLikedBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        if (!Global.isConnected(getContext())) {
            try {
                cards = initCardsFromLocalDb();
                Collections.reverse(cards);
                mCardArrayAdapter.setCards(cards);
                Global.showMessage(this.getContext(), "No internet conn",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvLikesCardRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

        // a liked balloon in Db?
        List<LikedBalloon> likedBalloonsListInDb = likedBalloonDao.queryForAll();
        if (likedBalloonsListInDb.size() > 0 && likedBalloonsListInDb != null) {
            for (int i = 0; i < likedBalloonsListInDb.size(); i++) {
                card = createCard(likedBalloonsListInDb.get(i));
                cards.add(card);
            }
        }
        return cards;
    }

    private Card createCard(Balloon balloon) {
        Card card = new CardLikes(getActivity(), balloon, savedInstanceState);
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
        likedBalloonList.addAll(balloonsMap.keySet());
        saveLikedBalloonsToDatabase(likedBalloonList);
    }

    private void saveLikedBalloonsToDatabase(List<LikedBalloon> balloonList) {

        if (balloonList.size() > 0 && balloonList != null) {

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    likedBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        loadLikedBalloons();
    }

    private void loadLikedBalloons() {
        new ReusableAsync<Integer>(this.getContext())
                .get("/balloons/liked")
                .bearer(Global.getApiToken(this.getContext()))
                .progressBar(progressBar)
                .onSuccess(new SuccessHandler<Integer>() {
                    @Override
                    public Integer handle(JSONObject jsonObject) throws JSONException {
                        JSONArray jsonArray = jsonObject.getJSONArray("balloons");
                        cards = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            LikedBalloon balloon = null;

                            Date sent_at = null;
                            try {
                                sent_at = dateFormat.parse(object.getString("sent_at"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            balloon = new LikedBalloon(object.getString("text"),
                                    object.getInt("balloon_id"), object.getDouble("sentiment"),
                                    object.getDouble("lat"), object.getDouble("lng"), sent_at);
                            Log.d(LikedMailsFragment.class.getSimpleName(), "lat: " + object.getDouble("lat"));
                            Log.d(LikedMailsFragment.class.getSimpleName(), "lng: " + object.getDouble("lng"));
                            balloon.setIs_creeped(object.getInt("creeped"));
                            balloon.setIs_refilled(object.getInt("refilled"));
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
}
