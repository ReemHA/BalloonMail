package com.balloonmail.app.balloonmailapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.balloonmail.app.balloonmailapp.fragments.LikedMailsFragment;
import com.balloonmail.app.balloonmailapp.R;
import com.balloonmail.app.balloonmailapp.fragments.ReceivedMailsFragment;
import com.balloonmail.app.balloonmailapp.fragments.SentMailsFragment;
import com.balloonmail.app.balloonmailapp.utilities.Global;
import com.balloonmail.app.balloonmailapp.models.DatabaseHelper;
import com.balloonmail.app.balloonmailapp.models.LikedBalloon;
import com.balloonmail.app.balloonmailapp.models.ReceivedBalloon;
import com.balloonmail.app.balloonmailapp.models.SentBalloon;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MailsTabbedActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseHelper dbHelper;
    private ReceivedMailsFragment receivedMailsFragment;
    private LikedMailsFragment likedMailsFragment;
    private SentMailsFragment sentMailsFragment;
    private int[] tabIcons = {
            R.drawable.ic_sent_white_48px, //Sent
            R.drawable.ic_received_white_48px, //Received
            R.drawable.ic_liked_white_48px, //Likes
    };
    private List<SentBalloon> sentBalloonList;
    private List<ReceivedBalloon> receivedBalloonList;
    private List<LikedBalloon> likedBalloonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mails_tabbed);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        receivedMailsFragment = new ReceivedMailsFragment();
        sentMailsFragment = new SentMailsFragment();
        likedMailsFragment = new LikedMailsFragment();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteMailActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
          /*
            set the number of pages that should be retained in the view hierarchy in the idle state
            (2 pages + the current page). To increase performance.
         */
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        int defaultValue = 1;
        int page = getIntent().getIntExtra(Global.ARG_MAILS_TABBED_TAG, defaultValue);
        viewPager.setCurrentItem(page);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(sentMailsFragment, "Sent");
        adapter.addFragment(receivedMailsFragment, "Received");
        adapter.addFragment(likedMailsFragment, "Likes");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_mails_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent intent = new Intent(MailsTabbedActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        //saveReceivedBalloonsToDatabase(receivedBalloonList);
        //saveLikedBalloonsToDatabase(likedBalloonList);

    }

    private void saveSentBalloonsToDatabase(List<SentBalloon> balloonList) {
        if (balloonList.size() > 0 && balloonList != null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            Dao<SentBalloon, Integer> sentBalloonDao = null;
            try {
                sentBalloonDao = dbHelper.getSentBalloonDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    sentBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();
        }
    }

    private void saveReceivedBalloonsToDatabase(List<ReceivedBalloon> balloonList) {
        if (balloonList.size() > 0 && balloonList != null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            Dao<ReceivedBalloon, Integer> receivedBalloonDao = null;
            try {
                receivedBalloonDao = dbHelper.getReceivedBalloonDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    receivedBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();
        }
    }

    private void saveLikedBalloonsToDatabase(List<LikedBalloon> balloonList) {
        if (balloonList.size() > 0 && balloonList != null) {
            dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            Dao<LikedBalloon, Integer> likedBalloonDao = null;
            try {
                likedBalloonDao = dbHelper.getLikedBalloonDao();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // save balloon onto db
            for (int i = 0; i < balloonList.size(); i++) {
                try {
                    likedBalloonDao.create(balloonList.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            OpenHelperManager.releaseHelper();

        }
    }

}
