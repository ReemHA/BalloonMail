package com.balloonmail.app.balloonmailapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.Utilities.Global;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CardSent extends Card {
    Balloon balloon;
    private ProgressDialog mProgressDialog;
    public CardSent(Balloon balloon, Context context) {
        super(context, R.layout.card_sent_item);
        this.balloon = balloon;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.sentBalloonTextTv);
            ImageButton mapBtn = (ImageButton) view.findViewById(R.id.mapImageButton);

            if (mTitleView != null && mapBtn != null) {
                mTitleView.setText(balloon.getText());

                ViewToClickToExpand viewToClickToExpand =
                        ViewToClickToExpand.builder()
                                .setupView(mapBtn);
                setViewToClickToExpand(viewToClickToExpand);
            }

            TextView refill = (TextView) view.findViewById(R.id.refillTv);
            refill.setText(String.valueOf(balloon.getRefills()) + " refills");

            //TextView reach = (TextView)view.findViewById(R.id.reachTv);
            //reach.setText(String.valueOf(balloon.getReach()) + " reach");

            TextView creep = (TextView) view.findViewById(R.id.creepTv);
            creep.setText(String.valueOf(balloon.getCreeps()) + " creeps");

            ImageButton mapButton = (ImageButton) view.findViewById(R.id.mapImageButton);
            mapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawPaths();
                }
            });
        }
    }

    private void drawPaths() {
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Getting your updated balloons..");
        mProgressDialog.show();
        new GetAllPathsOfSource().execute();
    }

    private class GetAllPathsOfSource extends AsyncTask<Void, Void, Void> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                url = new URL(Global.SERVER_URL + "/balloons/received");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer " + Global.USER_API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                try {
                    connection.connect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                getResponse();
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
            HashMap<LatLng, ArrayList<LatLng>> paths = new HashMap<>();

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
            JSONArray jsonArray = jsonObject.getJSONArray("paths");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                LatLng source = new LatLng(object.getDouble("from_lat"), object.getDouble("from_lng"));
                ArrayList<LatLng> dests = paths.get(source);
                if (!paths.containsKey(source)) {
                    dests = new ArrayList<>();
                    paths.put(source, dests);
                }
                dests.add(new LatLng(object.getDouble("to_lat"), object.getDouble("to_lng")));
            }
            balloon.setDestinationsHashMap(paths);

            return;
        }
    }
}