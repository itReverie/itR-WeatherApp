package itreverie.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class detail_activity extends ActionBarActivity {

    public static final String DATE_KEY = "forecast_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);


        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            String date = getIntent().getStringExtra(DATE_KEY);

            Bundle arguments = new Bundle();
            arguments.putString(detail_activity.DATE_KEY, date);

            detail_fragment fragment = new detail_fragment();
            fragment.setArguments(arguments);

            //We will always work with the fragment manager whether we use intents or arguments
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_settings, menu);
        return true;
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
        return super.onOptionsItemSelected(item);
    }
}
