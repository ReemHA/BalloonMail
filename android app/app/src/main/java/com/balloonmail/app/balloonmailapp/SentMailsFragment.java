package com.balloonmail.app.balloonmailapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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

public class SentMailsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "SentMailsFragment";
    private DatabaseHelper dbHelper;
    public SentMailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sent_mails, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.sent_recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        try {
            mAdapter = new SentRecyclerViewAdapter(getDataSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(mAdapter);

        //RecyclerView.ItemDecoration itemDecoration =
        //        new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);

        //((SentRecyclerViewAdapter)mAdapter).addItem(new Balloons("Example Text Message"), 0);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((SentRecyclerViewAdapter) mAdapter).setOnItemClickListener(
                new SentRecyclerViewAdapter.MyClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {
                        Log.i(LOG_TAG, " Clicked on Item " + position);
                    }
                });
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
