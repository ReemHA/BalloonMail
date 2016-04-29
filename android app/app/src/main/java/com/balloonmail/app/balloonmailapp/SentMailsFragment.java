package com.balloonmail.app.balloonmailapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmailapp.adapters.SentRecyclerViewAdapter;
import com.balloonmail.app.balloonmailapp.models.Balloons;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.SentBalloons;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class SentMailsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "SentMailsFragment";
    ArrayList<Card> cards;

    View rootView;

    private DatabaseHelper dbHelper;
    public SentMailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sent_mails, container, false);

        cards = new ArrayList<Card>();
        cards = initCards();

        CardArrayRecyclerViewAdapter mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvCardRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

        return rootView;
    }

    public ArrayList<Card> initCards(){
        ArrayList<Card> cards = new ArrayList<Card>();

        for(int i=0; i<10; i++){
            Balloon balloon = new Balloon("This is an example of text message no. " + i + " sent by a balloon.");
            Card card = new CardSent(getActivity().getBaseContext(), balloon);
            //CardHeader cardHeader = new CustomSentHeaderCard(getActivity().getBaseContext());
            //cardHeader.setButtonExpandVisible(true);
            //card.addCardHeader(cardHeader);

            CardExpand cardExpand = new CustomSentExpandCard(getActivity().getBaseContext());
            card.addCardExpand(cardExpand);

            card.setCardElevation(getResources().getDimension(R.dimen.card_shadow_elevation));

            cards.add(card);
        }

        return cards;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ArrayList<Balloons> getDataSet() throws SQLException {
        dbHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        Dao<SentBalloons, Integer> sentBalloonsDao = dbHelper.getSentBalloonDao();

        // query will return a list
        List<SentBalloons> sentBalloonsList = sentBalloonsDao.queryForAll();

        ArrayList results = new ArrayList<>();
        for (int index = 0; index < sentBalloonsList.size(); index++) {
            results.add(index, sentBalloonsList.get(index));
        }

        OpenHelperManager.releaseHelper();
        return results;
    }
}
