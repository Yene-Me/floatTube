package org;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import hera.yene.org.R;

import java.util.ArrayList;
import java.util.Map;

import org.tracker.AnalyticsApplication;
import org.util.helper;


/**
 * Created by yene on 01/05/2017.
 */

public class MainActivity extends Activity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPlanetTitles;
    private Map<String, String> mPlayList;
    private ArrayList<String> viewListName;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private AdView mAdView;
    private Tracker mTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupTracker();
        connectToFireBaseDB();
        setUpDraw(savedInstanceState);
        mobileAds();
        trackEvents("Current View", "load main Page");

        searchQuery(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        setIntent(intent);
        searchQuery(intent);
    }

    private void searchQuery(Intent intent)
    {
        // Get the intent, verify the action and get the query;
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
            SearchItem(query);
            Log.e("The read query: ", "" + query);
        }
    }


    private void setupTracker()
    {
        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Main Page Video List View");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }


    private void trackEvents(String category, String action)
    {
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    private void mobileAds()
    {
        MobileAds.initialize(this, "ca-app-pub-9029444930969192/9967190262");
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded()
            {
                // Code to be executed when an ad finishes loading.
                trackEvents("Ads", "onAdLoaded");

            }

            @Override
            public void onAdFailedToLoad(int errorCode)
            {
                // Code to be executed when an ad request fails.
                trackEvents("Ads", "onAdFailedToLoad");

            }

            @Override
            public void onAdOpened()
            {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                trackEvents("Ads", "onAdLoaded");
            }

            @Override
            public void onAdLeftApplication()
            {
                // Code to be executed when the user has left the app.
                trackEvents("Ads", "onAdLeftApplication");
            }

            @Override
            public void onAdClosed()
            {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
                trackEvents("Ads", "onAdClosed");
            }
        });
    }

    private void setUpDraw(Bundle savedInstanceState)
    {
        mTitle = mDrawerTitle = getTitle();
        //mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view)
            {
                //getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()

                trackEvents("draw", "open");
            }

            public void onDrawerOpened(View drawerView)
            {
                trackEvents("draw", "close");
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    private void connectToFireBaseDB()
    {

        DatabaseReference ref = database.getReference("playlist");
        trackEvents("DB", "Call To FireBase");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                mPlayList = (Map) dataSnapshot.getValue();

                viewListName = new ArrayList<>();


                for (Map.Entry<String, String> entry : mPlayList.entrySet()) {
                    viewListName.add(entry.getKey());
                }

                mDrawerList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
                        R.layout.drawer_list_item, viewListName));

                // default load the first channel from the list
                selectItem(0);
            }


            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                Log.e("The read failed: ", "" + databaseError.getCode());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_websearch).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        else
            {
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        database.goOffline();
        trackEvents("DB", "pause off-Line");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        database.goOffline();
        trackEvents("DB", "destroy off-Line");
    }


    @Override
    protected void onRestart()
    {
        super.onRestart();
        database.goOnline();
        trackEvents("DB", "restart off-Line");
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        database.goOnline();
        trackEvents("DB", "resume on-Line");
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            selectItem(position);
        }
    }

    private void selectItem(int position)
    {
        // update the main content by replacing fragments
        Fragment fragment = new VideoListView.VideoListFragment();
        Bundle args = new Bundle();
        if (viewListName != null) {
            Log.e("position", "" + mPlayList.get(viewListName.get(position)));
            args.putString(helper.VIDEO_ID, "" + mPlayList.get(viewListName.get(position)));
            fragment.setArguments(args);
            trackEvents("CurrentView", "Video player");
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void SearchItem(String term)
    {
        Fragment fragment = new VideoListView.VideoListFragment();
        Bundle args = new Bundle();
        if(term.length()!=0)
        {
            args.putString(helper.VIDEO_SEARCH_TERM, term);
            fragment.setArguments(args);
        }

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    private void loadPlayList()
    {

    }

    @Override
    public void setTitle(CharSequence title)
    {
        //mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


}
