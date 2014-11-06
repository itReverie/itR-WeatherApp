package itreverie.weatherapp;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.ApplicationTestCase;

import java.util.Map;
import java.util.Set;

import itreverie.processing.WeatherDataParser;
import itreverie.weatherapp.data.WeatherContract;
import itreverie.weatherapp.data.WeatherDbHelper;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */


public class TestDb extends ApplicationTestCase<Application> {

    private static final String LOG_TAG = WeatherDataParser.class.getSimpleName();

    //static public String TEST_CITY_NAME="North Pole";
    static public String TEST_LOCATION="99705";
    static public String TEST_DATE="20141205";
    //static public String TEST_START_DATE="20141205";

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }

    public TestDb() {
        super(Application.class);
    }


    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    /*
    public void testInsertReadDb() {

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();



        //ADDING LOCATION VALUES
        ContentValues testValues = createNorthPoleLocationValues();

        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, testValues);






        // ADDING WEATHER VALUES
        // Fantastic.  Now that we have a location, add some WEATHER!
        ContentValues weatherValues = createWeatherValues(locationRowId);

        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherValues);
        assertTrue(weatherRowId != -1);

        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = db.query(
                WeatherContract.WeatherEntry.TABLE_NAME,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null  // sort order
        );

        validateCursor(weatherCursor, weatherValues);




        // Add the location values in with the weather data so that we can make
        // sure that the join worked and we actually get all the values back
        addAllContentValues(weatherValues, testValues);

        // Get the joined Weather and Location data
        weatherCursor = mContext.getContentResolver().query(
                WeatherContract.WeatherEntry.buildWeatherLocation(TestDb.TEST_LOCATION),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestDb.validateCursor(weatherCursor, weatherValues);

        dbHelper.close();
    }
    */


    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS, "99705");
        testValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(WeatherContract.LocationEntry.COLUMN_CORD_LATITUDE, 64.7488);
        testValues.put(WeatherContract.LocationEntry.COLUMN_CORD_LONGITUDE, -147.353);

        return testValues;
    }

    static ContentValues createWeatherValues(long testLocation)
    {
        String testDateText="20141205";
        double testDegrees=1.1;
        double testHumidity=1.2;
        double testPressure=1.3;
        int testMaxTemp=75;
        int testMinTemp=65;
        String testShortDesc="Asteroids";
        double testWindSpeed=5.5;
        long testWeatherId=321;

        // Fantastic.  Now that we have a location, add some weather!
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY,    testLocation);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT,   testDateText);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES,    testDegrees);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY,   testHumidity);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE,   testPressure);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,   testMaxTemp);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,   testMinTemp);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, testShortDesc);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, testWindSpeed);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, testWeatherId);

        return weatherValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }

}