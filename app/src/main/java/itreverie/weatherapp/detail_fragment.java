package itreverie.weatherapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import itreverie.weatherapp.data.WeatherContract;

/**
 * Created by Brenda on 11/24/2014.
 */
public class detail_fragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    ShareActionProvider mShareActionProvider;

    private static final String LOG_TAG=detail_fragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #itReverie-weatherApp";

    private SimpleCursorAdapter mForecastCursorAdapter;

    public static final int DETAIL_LOADER = 0;
    public static final String LOCATION_KEY="location";
    public static final String DATE_KEY = "forecast_date";


    private String mLocation;
    private String mForecastString;

    private ImageView mIconView ;
    private TextView   mDateView;
    private TextView      mFriendlyDateView;
    private TextView      mDescriptionView;
    private TextView      mHighTempView;
    private TextView      mLowTempView;
    private TextView      mHumidityView;
    private TextView      mWindView;
    private TextView      mPressureView;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
    };


    public detail_fragment() {
        setHasOptionsMenu(true);
    }


    //We use on instanceState to preserve the value of the location by saving the location into our Bundle
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }

    //In OnResume we can check if the location has changed
    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            //If the location has changed we need to restart the loader with the new URI
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Populate it with the detail fragment XML
        View rootView = inflater.inflate(R.layout.detail_fragment,container, false);

        mDateView= (TextView) rootView.findViewById(R.id.detail_date_day);
        mFriendlyDateView= (TextView) rootView.findViewById(R.id.detail_date_textview);


        mIconView = (ImageView) rootView.findViewById(R.id.list_item_icon);
        mDescriptionView= (TextView) rootView.findViewById(R.id.detail_forecast_textview);


        mHighTempView= (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView= (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView= (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView= (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView= (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater)
    {
        inflater.inflate(R.menu.share,menu);
        MenuItem menuItem= menu.findItem(R.id.action_share);
        //ShareActionProvider mShareActionProvider =menuItem.getActionProvider();
        mShareActionProvider=(ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        if(mShareActionProvider != null)
        {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
        else
        {
            Log.d(LOG_TAG, "Share Action Provider is null?");
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //String forecastDate= getActivity().getIntent().getDataString();
        Bundle extras = getActivity().getIntent().getExtras();
        String forecastDate = extras.getString(DATE_KEY);
        //The opposite would be put string
        //extras.putString(LOCATION_KEY, mLocation);


        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());

        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation, forecastDate);

        Log.v(LOG_TAG, weatherForLocationUri.toString());

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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!cursor.moveToFirst()) { return; }

        //ICON
        int weatherId= cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        //DAY
        String dateString = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));//Utility.formatDate(
        //((TextView) getView().findViewById(R.id.detail_date_textview)).setText(dateString);
        String friendlyDateText= Utility.getDayName(getActivity(),dateString);
        String dateText= Utility.getFormattedMonthDay(getActivity(),dateString);
        mFriendlyDateView.setText(dateText);
        mDateView.setText(friendlyDateText);

        //DATE
        String weatherDescription = cursor.getString(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        //((TextView) getView().findViewById(R.id.detail_forecast_textview)).setText(weatherDescription);
        mDescriptionView.setText(weatherDescription);

        boolean isMetric = Utility.isMetric(getActivity());

        //HIGHT TEMP
        String high = Utility.formatTemperature( getActivity(),cursor.getDouble(cursor.getColumnIndex( WeatherContract.WeatherEntry.COLUMN_MAX_TEMP)), isMetric);
        //((TextView) getView().findViewById(R.id.detail_high_textview)).setText(high);
        mHighTempView.setText(high);

        //LOW TEMP
        String low = Utility.formatTemperature( getActivity(),cursor.getDouble(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP)), isMetric);
        //((TextView) getView().findViewById(R.id.detail_low_textview)).setText(low);
        mLowTempView.setText(low);

        //HUMIDITY
        float humidity = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_HUMIDITY));
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        //WIND SPEED AND DIRECTION
        float windSpeedStr = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED));
        float windDirStr = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DEGREES));
        mWindView.setText(Utility.getFormattedWind(getActivity(),windSpeedStr,windDirStr));

        //PRESSURE
        float pressure = cursor.getFloat(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_PRESSURE));
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));




        // We still need this for the share intent
        mForecastString = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        Log.v(LOG_TAG, "Forecast String: " + mForecastString);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
