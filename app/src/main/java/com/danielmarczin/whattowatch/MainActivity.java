package com.danielmarczin.whattowatch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.danielmarczin.whattowatch.fragments.ChooseFragment;
import com.danielmarczin.whattowatch.fragments.StatsFragment;
import com.danielmarczin.whattowatch.fragments.history.HistoryFragment;
import com.danielmarczin.whattowatch.util.ShowCaseQueue;
import com.danielmarczin.whattowatch.util.Util;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int CHOOSE_POS = 0;
    private static final int HISTORY_POS = 1;
    private static final int STATS_POS = 2;
    private Fragment[] fragments;

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_choose:
                    switchFragment(fragments[CHOOSE_POS]);
                    return true;
                case R.id.navigation_history:
                    switchFragment(fragments[HISTORY_POS]);
                    return true;
                case R.id.navigation_stats:
                    switchFragment(fragments[STATS_POS]);
                    return true;
            }
            return false;
        }
    };

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, fragment)
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (fragments == null) {
            fragments = new Fragment[3];
            fragments[CHOOSE_POS] = new ChooseFragment();
            fragments[HISTORY_POS] = new HistoryFragment();
            fragments[STATS_POS] = new StatsFragment();
        }

        if (savedInstanceState == null) {
            switchFragment(fragments[CHOOSE_POS]);
        }

        if (Util.checkFirstRun(MainActivity.this, TAG)) { //check shared prefs
            ShowCaseQueue showCaseQueue = new ShowCaseQueue(MainActivity.this);
            showCaseQueue.addTutorials(R.id.navigation_choose, R.string.tut_choose_primary,
                    R.string.tut_choose_secondary);
            showCaseQueue.addTutorials(R.id.navigation_history, R.string.tut_history_primary,
                    R.string.tut_history_secondary);
            showCaseQueue.addTutorials(R.id.navigation_stats, R.string.tut_stats_primary,
                    R.string.tut_stats_secondary);
            showCaseQueue.start();
        }

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                showAbout();
                return true;
            case R.id.menu_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showHelp() {
        LinkDialogFragment alertDialogFragment = LinkDialogFragment.newInstance(R.string.help,
                R.string.help_text);
        alertDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void showAbout() {
        LinkDialogFragment alertDialogFragment = LinkDialogFragment.newInstance(R.string.about,
                R.string.about_text);
        alertDialogFragment.show(getSupportFragmentManager(), "dialog");
    }
}
