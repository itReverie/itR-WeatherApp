package itreverie.weatherapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brenda on 10/2/2014.
 */
public class WeatherDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION=1;
    public static final String DATABASE_NAME="sunshine.db";

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude

        // TBD


        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +
                WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS + " TEXT UNIQUE NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT UNIQUE  NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_CORD_LATITUDE + " REAL NOT NULL, " +
                WeatherContract.LocationEntry.COLUMN_CORD_LONGITUDE + " REAL NOT NULL," +
                " UNIQUE (" + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTINGS +    ") ON CONFLICT IGNORE"+ " );";

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DATETEXT + " TEXT UNIQUE NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " + WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), " +

                // To assure the application have just one weather entry per day
                // per location, it's created a UNIQUE constraint with REPLACE strategy
                " UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
