package com.balloonmail.app.balloonmailapp.fragments;

import android.support.v4.app.Fragment;
//
public class LikedMailsFragment extends Fragment {
//
//    private ArrayList<Card> cards;
//    private HashMap<LikedBalloon, Card> balloonsMap;
//    private static List<LikedBalloon> likedBalloonList;
//    private DatabaseHelper dbHelper;
//    private Dao<LikedBalloon, Integer> likedBalloonDao;
//    private DateFormat dateFormat;
//    View rootView;
//    Bundle savedInstanceState;
//    ProgressBar progressBar;
//    SwipeRefreshLayout swipeRefreshLayout;
//    CardArrayRecyclerViewAdapter mCardArrayAdapter;
//    ImageView emptyStateImage;
//    private Context context;
//
//
//
//    public LikedMailsFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        this.context = getContext();
//        rootView = inflater.inflate(R.layout.fragment_liked_mails, container, false);
//
//        this.savedInstanceState = savedInstanceState;
//        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_id);
//
//        dbHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
//        dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
//        balloonsMap = new HashMap<>();
//        cards = new ArrayList<>();
//        likedBalloonList = new ArrayList<>();
//        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadLikedBalloons();
//            }
//        });
//
//        // Doa of Liked table
//        likedBalloonDao = null;
//        try {
//            likedBalloonDao = dbHelper.getLikedBalloonDao();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        mCardArrayAdapter = new CardArrayRecyclerViewAdapter(getActivity(), cards);
//
//        if (!Global.isConnected(getContext())) {
//            try {
//                cards = initCardsFromLocalDb();
//                mCardArrayAdapter.setCards(cards);
//                Global.showMessage(this.getContext(), "No internet conn",
//                        Global.ERROR_MSG.SERVER_CONN_FAIL.getMsg());
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        } else {
//            loadLikedBalloons();
//        }
//
//        //Staggered grid view
//        CardRecyclerView mRecyclerView = (CardRecyclerView) rootView.findViewById(R.id.cvLikesCardRecyclerView);
//        mRecyclerView.setHasFixedSize(false);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        //Set the empty view
//        if (mRecyclerView != null) {
//            mRecyclerView.setAdapter(mCardArrayAdapter);
//        }
//
//        emptyStateImage = (ImageView) rootView.findViewById(R.id.emptyStateImage);
//        return rootView;
//    }
//
//    public ArrayList<Card> initCardsFromLocalDb() throws SQLException {
//
//        ArrayList<Card> cards = new ArrayList<>();
//        Card card;
//
//        // a liked balloon in Db?
//        QueryBuilder<LikedBalloon, Integer> q = likedBalloonDao.queryBuilder();
//        q.orderBy("sent_at", false);
//        List<LikedBalloon> likedBalloonsListInDb = q.query();
//        //List<LikedBalloon> likedBalloonsListInDb = likedBalloonDao.queryForAll();
//        if (likedBalloonsListInDb.size() > 0 && likedBalloonsListInDb != null) {
//            for (int i = 0; i < likedBalloonsListInDb.size(); i++) {
//                card = createCard(likedBalloonsListInDb.get(i));
//                cards.add(card);
//            }
//        }
//
//        //reset balloon object in holder
//        Global.balloonHolder.setBalloon(null);
//
//        return cards;
//    }
//
//    private Card createCard(final LikedBalloon balloon) {
//        Card card = new CardLikes(getActivity(), balloon, savedInstanceState);
//        card.setOnClickListener(new Card.OnCardClickListener() {
//            @Override
//            public void onClick(Card card, View view) {
//                Intent intent = new Intent(getContext(), ReceivedAndLikedMailDetailsActivity.class);
//                Global.balloonHolder.setBalloon(balloon);
//                intent.putExtra(Global.RECEIVED_OR_LIKED, "l");
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                getContext().startActivity(intent);
//            }
//        });
//        card.setCardElevation(getResources().getDimension(R.dimen.card_shadow_elevation));
//        return card;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        //likedBalloonList.clear();
//        likedBalloonList.addAll(balloonsMap.keySet());
//        saveLikedBalloonsToDatabase(likedBalloonList);
//    }
//
//    private void saveLikedBalloonsToDatabase(List<LikedBalloon> balloonList) {
//
//        if (balloonList.size() > 0 && balloonList != null) {
//
//            // save balloon onto db
//            for (int i = 0; i < balloonList.size(); i++) {
//                try {
//                    likedBalloonDao.create(balloonList.get(i));
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//            //OpenHelperManager.releaseHelper();
//        }
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//    }
//
//    private void loadLikedBalloons() {
//        new ReusableAsync<Integer>(context)
//                .get("/balloons/liked")
//                .bearer(Global.getApiToken(context))
//                .progressBar(progressBar)
//                .onSuccess(new SuccessHandler<Integer>() {
//                    @Override
//                    public Integer handle(JSONObject jsonObject) throws JSONException {
//                        JSONArray jsonArray = jsonObject.getJSONArray("balloons");
//                        cards = new ArrayList<>();
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject object = jsonArray.getJSONObject(i);
//                            LikedBalloon balloon = null;
//
//                            Date sent_at = null;
//                            try {
//                                sent_at = dateFormat.parse(object.getString("sent_at"));
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//                            balloon = new LikedBalloon(object.getString("text"),
//                                    object.getInt("balloon_id"), object.getDouble("sentiment"),
//                                    object.getDouble("lat"), object.getDouble("lng"), sent_at);
//                            Log.d(LikedMailsFragment.class.getSimpleName(), "lat: " + object.getDouble("lat"));
//                            Log.d(LikedMailsFragment.class.getSimpleName(), "lng: " + object.getDouble("lng"));
//                            balloon.setIs_creeped(object.getInt("creeped"));
//                            balloon.setIs_refilled(object.getInt("refilled"));
//                            Card card = createCard(balloon);
//                            cards.add(card);
//                            balloonsMap.put(balloon, card);
//                        }
//                        return jsonArray.length();
//                    }
//                })
//                .onPost(new PostHandler<Integer>() {
//                    @Override
//                    public void handle(Integer aVoid) {
//                        if (swipeRefreshLayout.isRefreshing()) {
//                            swipeRefreshLayout.setRefreshing(false);
//                        }
//                        if (aVoid != null) {
//                            if (aVoid == 0) {
//                                emptyStateImage.setBackgroundResource(R.drawable.empty_state);
//                            } else {
//                                emptyStateImage.setBackgroundResource(0);
//                            }
//                        }
//
//                        Collections.reverse(cards);
//                        mCardArrayAdapter.setCards(cards);
//                        mCardArrayAdapter.notifyDataSetChanged();
//                    }
//                })
//                .send();
//    }
}
