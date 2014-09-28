package itreverie.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;






public class detail_activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Detail_Fragment())
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class Detail_Fragment extends Fragment {

        private static final String LOG_TAG=Detail_Fragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForecastString;

        public Detail_Fragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            //Populate it with the detail fragment XML
            View rootView = inflater.inflate(R.layout.detail_fragment,container, false);

            //Get the intent
            Intent intent= getActivity().getIntent();

            //If the intent is different that null and it has an extra text
            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT))
            {
                //Write that text in the Text View
                mForecastString= intent.getStringExtra(Intent.EXTRA_TEXT);
                ((TextView) rootView.findViewById(R.id.detail_text)).setText(mForecastString);
            }

            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
        {
            inflater.inflate(R.menu.share,menu);

            MenuItem menuItem= menu.findItem(R.id.action_share);

            //ShareActionProvider mShareActionProvider =menuItem.getActionProvider();

            ShareActionProvider mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

            if(mShareActionProvider != null)
            {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
            else
            {
                Log.d(LOG_TAG,"Share Action Provider is null?");
            }

        }

        private Intent createShareForecastIntent()
        {
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,mForecastString+FORECAST_SHARE_HASHTAG);
            return  shareIntent;
        }
    }
}
