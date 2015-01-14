package itreverie.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;


public class main_activity extends ActionBarActivity implements main_fragment.Callback {

    private final String LOG_TAG = main_activity.class.getSimpleName();
    private Boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.main_activity);

        //If it finds a detail container in the xml
        if(findViewById(R.id.weather_detail_container) != null)
        {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            if(savedInstanceState == null)
            {
                // In two-pane mode, show the detail view in this activity by
                // adding or replacing the detail fragment using a
                // fragment transaction.
                getSupportFragmentManager().beginTransaction().
                        replace(R.id.weather_detail_container, new detail_fragment())
                        .commit();
            }
        }
        else
        {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings, menu);
        getMenuInflater().inflate(R.menu.map_location, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,settings_activity.class));
            return true;
        }
        if (id == R.id.action_map_location) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap()
    {

        //-------------------------------------------------------
        //Another way to get the URI location
        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        //Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
        //        .appendQueryParameter("q", location)
        //        .build();
        //-------------------------------------------------------


        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String location=sharedPref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String geoLocation = String.format(Locale.ENGLISH, "geo:0,0?q=" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(geoLocation));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }

    @Override
    public void onItemSelected(String date) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putString(detail_fragment.DATE_KEY, date);

            detail_fragment fragment = new detail_fragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, detail_activity.class)
                    .putExtra(detail_activity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
