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


public class main_activity extends ActionBarActivity {

    private final String LOG_TAG = main_activity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.main_activity);

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
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String location=sharedPref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String geoLocation = String.format(Locale.ENGLISH, "geo:0,0?q=" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(geoLocation));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}
