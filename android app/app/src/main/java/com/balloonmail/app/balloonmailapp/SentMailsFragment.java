package com.balloonmail.app.balloonmailapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.balloonmail.app.balloonmailapp.Utilities.BalloonHolder;
import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.rest.RInterface;
import com.balloonmail.app.balloonmailapp.rest.model.SendBalloonRespond;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.recyclerview.internal.CardArrayRecyclerViewAdapter;
import it.gmariotti.cardslib.library.recyclerview.view.CardRecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SentMailsFragment extends Fragment {

    private ArrayList<Card> cards;
    private LinearLayoutManager mLayoutManager;
    private HashMap<SentBalloon, Card> hashMapForUpdates;
    private SentBalloonsListener mListener;
    private static boolean loading = false;
    CardArrayRecyclerViewAdapter mCardArrayAdapter;
    private final static int BALLOON_LIMIT = 4;
    private static DateFormat dateFormat;
    private static List<SentBalloon> sentBalloonList;
    View rootView;
    Bundle savedInstanceState;

    public SentMailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_sent_mails, container, false);
        hashMapForUpdates = new HashMap<>();
        cards = new ArrayList<>();
        cards = initCards();
        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
        dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
        this.savedInstanceState = savedInstanceState;

        //Staggered grid view
        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvCardRecyclerView);
        mRecyclerView.setHasFixedSize(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Set the empty view
        if (mRecyclerView != null) {
            mRecyclerView.setAdapter(mCardArrayAdapter);
        }

        sentBalloonList.addAll(hashMapForUpdates.keySet());
        mListener.getSentBalloons(sentBalloonList);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Toast.makeText(getContext(), "scrolling", Toast.LENGTH_SHORT).show();
                // get the current number of child views attached to the layout
                int currVisibleItemCount = mLayoutManager.getChildCount(); //8

                // get the total number of items in the layout
                int totalItemCount = mLayoutManager.getItemCount(); //8

                // get the adapter's position of the first visible view
                int pastVisibleItemCount = mLayoutManager.findFirstVisibleItemPosition(); //8

                if (!loading) {
                    if (currVisibleItemCount + pastVisibleItemCount >= totalItemCount) {
                        String last_date = mCardArrayAdapter.getItem(pastVisibleItemCount).getId();
                        try {
                            loadSentBalloons(last_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return rootView;
    }

    public ArrayList<Card> initCards() {
        ArrayList<Card> cards = new ArrayList<>();
        Card card;
        BalloonHolder balloonHolder = BalloonHolder.getInstance();
        if (balloonHolder.getBalloon() != null) {
            card = createCard(balloonHolder.getBalloon());
            cards.add(card);

            // to hash map
            hashMapForUpdates.put(balloonHolder.getBalloon(), card);
        }

        //reset balloon object in singleton class
        balloonHolder.setBalloon(null);
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
        card.setId(balloon.getSent_date().toString());
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
    }

    interface SentBalloonsListener {
        void getSentBalloons(List<SentBalloon> balloonList);
    }

    public void setListener(SentBalloonsListener listener) {
        mListener = listener;
    }

    private void loadSentBalloons(String last_date) throws ParseException {
        loading = true;
        Retrofit retrofit = Global.getRetrofit(getActivity());
        RInterface rInterface = retrofit.create(RInterface.class);
        if (!cards.isEmpty()) {
            Call<List<SendBalloonRespond>> call = rInterface.requestSentBalloonListWithDate(BALLOON_LIMIT, dateFormat.parse(last_date));
            call.enqueue(new Callback<List<SendBalloonRespond>>() {
                @Override
                public void onResponse(Call<List<SendBalloonRespond>> call, Response<List<SendBalloonRespond>> response) {
                    loading = false;
                    if (response != null) {
                        List<SendBalloonRespond> list = response.body();
                        for (int i = 0; i < list.size(); i++) {
                            SentBalloon sentBalloon = new SentBalloon(list.get(i).getText(), list.get(i).getBalloon_id(), list.get(i).getReach(),
                                    list.get(i).getCreeps(), list.get(i).getRefills(), list.get(i).getSentiment(),
                                    list.get(i).getSent_at());
                            mCardArrayAdapter.add(createCard(sentBalloon));
                        }
                    } else {
                        loading = true;
                        Log.d(SentMailsFragment.class.getSimpleName(), "Response is null");
                    }
                }

                @Override
                public void onFailure(Call<List<SendBalloonRespond>> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Log.d(SentMailsFragment.class.getSimpleName(),
                                "Server failure:" + t.getMessage());
                    }
                }
            });
        } else {
            Call<List<SendBalloonRespond>> call = rInterface.requestSentBalloonList(BALLOON_LIMIT);
            call.enqueue(new Callback<List<SendBalloonRespond>>() {
                @Override
                public void onResponse(Call<List<SendBalloonRespond>> call, Response<List<SendBalloonRespond>> response) {
                    loading = false;
                    if (response != null) {
                        List<SendBalloonRespond> list = response.body();
                        for (int i = 0; i < list.size(); i++) {
                            SentBalloon sentBalloon = new SentBalloon(list.get(i).getText(), list.get(i).getBalloon_id(), list.get(i).getReach(),
                                    list.get(i).getCreeps(), list.get(i).getRefills(), list.get(i).getSentiment(),
                                    list.get(i).getSent_at());
                            mCardArrayAdapter.add(createCard(sentBalloon));

                        }
                    } else {
                        loading = true;
                        Toast.makeText(getContext(), "Response is null", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<List<SendBalloonRespond>> call, Throwable t) {
                    if (t.getMessage() != null) {
                        Toast.makeText(getContext(), "Server failure:" + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
