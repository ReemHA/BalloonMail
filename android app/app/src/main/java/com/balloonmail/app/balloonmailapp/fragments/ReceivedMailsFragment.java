package com.balloonmail.app.balloonmailapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.balloonmail.app.balloonmailapp.CardReceived;
import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.activities.ReceivedMailDetailsActivity;
import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.manager.AppManager;
import com.balloonmail.app.balloonmailapp.manager.IBalloonLoadable;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
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


public class ReceivedMailsFragment extends Fragment implements IBalloonLoadable {
    private AppManager manager;
    private ArrayList<Card> cards;
    private LinearLayoutManager mLayoutManager;
    private HashMap<ReceivedBalloon, Card> balloonsMap;
    private static List<ReceivedBalloon> receivedBalloonList;
    private DatabaseHelper dbHelper;
    private Dao<ReceivedBalloon, Integer> receivedBalloonDao;
    private DateFormat dateFormat;
    private Context context;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
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
        this.context = getContext();
        rootView = inflater.inflate(R.layout.fragment_received_mails, container, false);
        this.savedInstanceState = savedInstanceState;
        dbHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_id);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
        balloonsMap = new HashMap<>();
        cards = new ArrayList<>();
        manager = AppManager.getInstance();
        receivedBalloonList = new ArrayList<>();
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                manager.loadBalloons(ReceivedMailsFragment.this);
            }
        });

        // Doa of Received table
        receivedBalloonDao = null;
        try {
            receivedBalloonDao = dbHelper.getReceivedBalloonDao();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        if (!Global.isConnected(context)) {
            try {
                cards = initCardsFromLocalDb();
                mCardArrayAdapter.setCards(cards);

                Global.showMessage(context, "No internet conn",
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            manager.loadBalloons(ReceivedMailsFragment.this);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        receivedBalloonList.addAll(balloonsMap.keySet());
        saveReceivedBalloonsToDatabase(receivedBalloonList);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void loadBalloons() {
        new ReusableAsync<Integer>(context)
                .get("/balloons/received")
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
                            sent_at = Global.getDateFromString(object.getString("sent_at"));
                            if (sent_at == null) {
                                throw new JSONException("error date format");
                            }
                            ReceivedBalloon balloon = new ReceivedBalloon(object.getString("text"), object.getInt("balloon_id"),
                                    object.getDouble("sentiment"), object.getDouble("lat"), object.getDouble("lng"),
                                    sent_at);
                            balloon.setIsLiked(object.getBoolean("liked")? 1:0);
                            balloon.setIsRefilled(object.getBoolean("refilled")? 1:0);
                            balloon.setIsCreeped(object.getBoolean("creeped")? 1:0);
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

    @Override
    public Card createCard(Balloon _balloon) {
        final ReceivedBalloon balloon = (ReceivedBalloon) _balloon;
        Card card = new CardReceived(balloon, getActivity(), savedInstanceState);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Intent intent = new Intent(getContext(), ReceivedMailDetailsActivity.class);
                Global.balloonHolder.setBalloon(balloon);
                intent.putExtra(Global.RECEIVED_OR_LIKED, "r");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        card.setCardElevation(getResources().getDimension(R.dimen.card_shadow_elevation));
        return card;
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
            //OpenHelperManager.releaseHelper();
        }
    }

    public ArrayList<Card> initCardsFromLocalDb() throws SQLException {
        ArrayList<Card> cards_temp = new ArrayList<>();
        Card card;

        // a received balloon in Db?
        QueryBuilder<ReceivedBalloon, Integer> q = receivedBalloonDao.queryBuilder();
        q.orderBy("sent_at", false);
        List<ReceivedBalloon> receivedBalloonsListInDb = q.query();
        //List<ReceivedBalloon> receivedBalloonsListInDb = receivedBalloonDao.queryForAll();
        if (receivedBalloonsListInDb.size() > 0 && receivedBalloonsListInDb != null) {
            for (int i = 0; i < receivedBalloonsListInDb.size(); i++) {
                card = createCard(receivedBalloonsListInDb.get(i));
                cards_temp.add(card);
            }
        }

        Global.balloonHolder.setBalloon(null);

        return cards_temp;
    }

}
