package com.balloonmail.app.balloonmailapp.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.models.Balloons;

import java.util.ArrayList;

/**
 * Created by Dalia on 4/23/2016.
 */
public class SentRecyclerViewAdapter extends RecyclerView.Adapter<SentRecyclerViewAdapter.BalloonHolder>{

    private static String LOG_TAG = "SentRecyclerView";
    private ArrayList<Balloons> sentBalloonsArrayList;
    private static MyClickListener myClickListener;

    public static class BalloonHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView text;

        public BalloonHolder(View itemView){
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.sent_mail_textView);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public SentRecyclerViewAdapter(ArrayList<Balloons> dataset) {
        sentBalloonsArrayList = dataset;
    }

    @Override
    public BalloonHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_sent_item, parent, false);

        BalloonHolder dataObjectHolder = new BalloonHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(BalloonHolder holder, int position) {
        holder.text.setText(sentBalloonsArrayList.get(position).getText());
    }

    public void addItem(Balloons dataObj, int index) {
        sentBalloonsArrayList.add(dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        sentBalloonsArrayList.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return sentBalloonsArrayList.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}
