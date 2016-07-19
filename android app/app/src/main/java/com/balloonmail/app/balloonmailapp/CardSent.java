package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
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
import it.gmariotti.cardslib.library.internal.CardExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CardSent extends Card {
    Balloon balloon;
    private SharedPreferences sharedPreferences;
    private static String api_token;
    private CardExpand cardExpand;
    CardSent _card;

    public CardSent(Balloon balloon, Context context) {
        super(context, R.layout.card_sent_item);
        this.balloon = balloon;
        sharedPreferences = context.getSharedPreferences(Global.USER_INFO_PREF_FILE, Context.MODE_PRIVATE);

        cardExpand = new CustomSentExpandCard(balloon, context, null);
        this.addCardExpand(cardExpand);
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view != null) {
            TextView mTitleView = (TextView) view.findViewById(R.id.sentBalloonTextTv);
            ImageButton mapBtn = (ImageButton) view.findViewById(R.id.mapImageButton);

            if (mTitleView != null && mapBtn != null) {
                mTitleView.setText(balloon.getText());

//                ViewToClickToExpand viewToClickToExpand =
//                        ViewToClickToExpand.builder()
//                                .setupView(mapBtn);
//                setViewToClickToExpand(viewToClickToExpand);
            }

            TextView refill = (TextView) view.findViewById(R.id.refillTv);
            refill.setText(String.valueOf(balloon.getRefills()) + " refills");

            //TextView reach = (TextView)view.findViewById(R.id.reachTv);
            //reach.setText(String.valueOf(balloon.getReach()) + " reach");

            api_token = sharedPreferences.getString(Global.PREF_USER_API_TOKEN, "");

            TextView creep = (TextView) view.findViewById(R.id.creepTv);
            creep.setText(String.valueOf(balloon.getCreeps()) + " creeps");

            _card = this;
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_card.isExpanded()) {
                        _card.doCollapse();
                        _card.setExpanded(false);
                    } else {
                        _card.setExpanded(true);
                        drawPaths();
                    }
                }
            });
        }
    }

    private void drawPaths() {
        new GetAllPathsOfSource().execute();
    }

    private class GetAllPathsOfSource extends AsyncTask<Void, Void, Void> {
        URL url;
        HttpURLConnection connection;
        String line;

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                url = new URL(Global.SERVER_URL + "/balloons/paths" + "?balloon_id=" + balloon.getBalloon_id());
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("authorization", "Bearer " + api_token);
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

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            if (!jsonObject.has("error")) {
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
            } else {
                Global.showMessage(getContext(), jsonObject.get("error").toString(),
                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
            }
            return;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            ((CustomSentExpandCard) cardExpand).setPathsOnMap(balloon);
            _card.doExpand();
        }
    }
}