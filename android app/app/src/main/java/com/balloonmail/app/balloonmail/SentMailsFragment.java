package com.balloonmail.app.balloonmail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balloonmail.app.balloonmail.adapters.SentRecyclerViewAdapter;
import com.balloonmail.app.balloonmail.models.Balloon;

import java.util.ArrayList;

public class SentMailsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "SentMailsFragment";

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

        mAdapter = new SentRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        //RecyclerView.ItemDecoration itemDecoration =
        //        new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        //mRecyclerView.addItemDecoration(itemDecoration);

        //((SentRecyclerViewAdapter)mAdapter).addItem(new Balloon("Example Text Message"), 0);
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

    private ArrayList<Balloon> getDataSet() {
        ArrayList results = new ArrayList<Balloon>();
        for (int index = 0; index < 20; index++) {
            Balloon obj = new Balloon("Example Text Message " + index);
            results.add(index, obj);
        }
        return results;
    }
}
