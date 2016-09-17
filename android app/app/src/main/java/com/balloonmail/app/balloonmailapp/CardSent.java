package com.balloonmail.app.balloonmailapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.balloonmail.app.balloonmailapp.async.PostHandler;
import com.balloonmail.app.balloonmailapp.async.ReusableAsync;
import com.balloonmail.app.balloonmailapp.async.SuccessHandler;
import com.balloonmail.app.balloonmailapp.models.Balloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardExpand;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;

/**
 * Created by Dalia on 4/25/2016.
 */
public class CardSent extends Card {
    Balloon balloon;
    private CardExpand cardExpand;
    CardSent _card;

    public CardSent(Balloon balloon, Context context) {
        super(context, R.layout.card_sent_item);
        this.balloon = balloon;
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
            }

            TextView refill = (TextView) view.findViewById(R.id.refillTv);
            refill.setText(String.valueOf(balloon.getRefills()) + " refills");
            TextView creep = (TextView) view.findViewById(R.id.creepTv);
            creep.setText(String.valueOf(balloon.getCreeps()) + " creeps");

            _card = this;
            /* mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_card.isExpanded()) {
                        _card.doCollapse();
                        _card.setExpanded(false);
                    } else {
                        _card.setExpanded(true);
                        _card.doExpand();
                        drawPaths();
                    }
                }
            }); */
            ViewToClickToExpand viewToClickToExpand =
                    ViewToClickToExpand.builder()
                            .setupView(mapBtn)
                            .highlightView(false);
            setViewToClickToExpand(viewToClickToExpand);

            _card.setOnExpandAnimatorEndListener(new OnExpandAnimatorEndListener() {
                @Override
                public void onExpandEnd(Card card) {
                    drawPaths();
                }
            });
        }
    }

    private void drawPaths() {
        new ReusableAsync<Void>(this.getContext())
                .get("/balloons/paths")
                .bearer(Global.getApiToken(this.getContext()))
                .addQuery("balloon_id", Integer.toString(balloon.getBalloon_id()))
                .onSuccess(new SuccessHandler<Void>() {
                    @Override
                    public Void handle(JSONObject jsonObject) throws JSONException {
                        HashMap<LatLng, ArrayList<LatLng>> paths = new HashMap<>();
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
                        return null;
                    }
                })
                .onPost(new PostHandler <Void>() {
                    @Override
                    public void handle(Void data) {
                        ((CustomSentExpandCard) cardExpand).setPathsOnMap(balloon);
                        _card.doExpand();
                    }
                })
                .send();
    }}