package itreverie.weatherapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Date;
import java.util.Locale;

import itreverie.weatherapp.data.WeatherContract;



public class main_activity extends ActionBarActivity {

    private final String LOG_TAG = main_activity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainActivityContainer, new main_fragment())
                    .commit();
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
        SharedPreferences sharedPref= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String location=sharedPref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
        String geoLocation = String.format(Locale.ENGLISH, "geo:0,0?q=" + location);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(geoLocation));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class main_fragment extends Fragment implements LoaderCallbacks<Cursor> {
       private SimpleCursorAdapter mForecastAdapter;
       private static final int FORECAST_LOADER = 0;
       private String mLocation;


        // For the forecast view we're showing only a small subset of the stored data.
        // Specify the columns we need.
        private static final String[] FORECAST_COLUMNS = {
                // In this case the id needs to be fully qualified with a table name, since
                // the content provider joins the location & weather tables in the background
                // (both have an _id column)
                // On the one hand, that's annoying.  On the other, you can search the weather table
                // using the location set by the user, which is only in the Location table.
                // So the convenience is worth it.
                WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
                WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS
        };

        // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
        // must change.
        public static final int COL_WEATHER_ID = 0;
        public static final int COL_WEATHER_DATE = 1;
        public static final int COL_WEATHER_DESC = 2;
        public static final int COL_WEATHER_MAX_TEMP = 3;
        public static final int COL_WEATHER_MIN_TEMP = 4;
        public static final int COL_LOCATION_SETTING = 5;

        //CONSTRUCTOR
        public main_fragment() {
        }

        //EXTRA BUTTONS IN THE MENU
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.refresh, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_refresh) {
                updateWeather();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }



        //LIST ITEMS IN THE MAIN LAYOUT
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            // The SimpleCursorAdapter will take data from the database through the
            // Loader and use it to populate the ListView it's attached to.
            mForecastAdapter = new SimpleCursorAdapter(
                    getActivity(),//Context
                    R.layout.list_item_forecast,//Layout
                    null,
                    // FROM: the column names to use to fill the textviews
                    new String[]{WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                    },
                    //  TO: the textviews to fill with the data pulled from the columns above
                    new int[]{R.id.list_item_date_textview,
                            R.id.list_item_forecast_textview,
                            R.id.list_item_high_textview,
                            R.id.list_item_low_textview
                    },
                    0
            );

            mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                    boolean isMetric = Utility.isMetric(getActivity());
                    switch (columnIndex) {
                        case COL_WEATHER_MAX_TEMP:
                        case COL_WEATHER_MIN_TEMP: {
                            // we have to do some formatting and possibly a conversion
                            ((TextView) view).setText(Utility.formatTemperature(
                                    cursor.getDouble(columnIndex), isMetric));
                            return true;
                        }
                        case COL_WEATHER_DATE: {
                            String dateString = cursor.getString(columnIndex);
                            TextView dateView = (TextView) view;
                            dateView.setText(Utility.formatDate(dateString));
                            return true;
                        }
                    }
                    return false;
                }
            });

            //SETTING THE INFORMATION FOR THE ITEM DETAILS IN THE VIEW
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);

            ListView listView = ((ListView) rootView.findViewById(R.id.list_view_forecast));
            listView.setAdapter(mForecastAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = mForecastAdapter.getCursor();
                    if (cursor != null && cursor.moveToPosition(position)) {
                        String dateString = Utility.formatDate(cursor.getString(COL_WEATHER_DATE));
                        String weatherDescription = cursor.getString(COL_WEATHER_DESC);

                        boolean isMetric = Utility.isMetric(getActivity());
                        String high = Utility.formatTemperature(
                                cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                        String low = Utility.formatTemperature(
                                cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                        String detailString = String.format("%s - %s - %s/%s",
                                dateString, weatherDescription, high, low);

                        Intent intent = new Intent(getActivity(), detail_activity.class)
                                .putExtra(Intent.EXTRA_TEXT, detailString);
                        startActivity(intent);
                    }
                }
            });
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(FORECAST_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        private void updateWeather() {
            String location = Utility.getPreferredLocation(getActivity());
            new FetchWeatherTask(getActivity()).execute(location);
        }

        @Override
        public void onResume() {
            super.onResume();
            String preferedLocation=Utility.getPreferredLocation(getActivity());
            if (mLocation != null && !mLocation.equals(preferedLocation)) {
                getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
            }
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // This is called when a new Loader needs to be created.  This
            // fragment only uses one loader, so we don't care about checking the id.

            // To only show current and future dates, get the String representation for today,
            // and filter the query to return weather only for dates after or including today.
            // Only return data after today.
            String startDate = WeatherContract.getDbDateString(new Date());

            // Sort order:  Ascending, by date.
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

            mLocation = Utility.getPreferredLocation(getActivity());
            Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(mLocation, startDate);

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    sortOrder
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            mForecastAdapter.swapCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
            //Clears the data
            mForecastAdapter.swapCursor(null);
        }
    }
}
