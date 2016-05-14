package com.balloonmail.app.balloonmailapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;

import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;

public class LikedMailsFragment extends Fragment {

    private static String LOG_TAG = "LikedMailsFragment";
    ArrayList<Card> cards;

    View rootView;

    public LikedMailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_liked_mails, container, false);

        cards = new ArrayList<Card>();

        cards = initCards();

        CardArrayRecyclerViewAdapter mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);

        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvLikesCardRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

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

    public ArrayList<Card> initCards(){
        ArrayList<Card> cards = new ArrayList<>();

        ArrayList<Balloon> balloonArrayList = getDataSet();
        for(int i=0; i<balloonArrayList.size(); i++){
            cards.add(createCard(balloonArrayList.get(i)));
        }

        return cards;
    }

    private Card createCard(Balloon balloon){
        Card card = new CardLikes(getActivity().getBaseContext(), balloon);
        //CardHeader cardHeader = new CustomSentHeaderCard(getActivity().getBaseContext());
        //cardHeader.setButtonExpandVisible(true);
        //card.addCardHeader(cardHeader);

        card.setCardElevation(getResources().getDimension(R.dimen.card_shadow_elevation));

        return card;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ArrayList<Balloon> getDataSet() {

        ArrayList results = new ArrayList<>();
        for (int index = 0; index < 10; index++) {
            results.add(index, new LikedBalloon("Like no. " + index, 0, 0 ,0));
        }

        return results;
    }

}
