package itreverie.weatherapp.data;


import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Brenda on 10/2/2014.
 */
public class WeatherContract {


    //CONTENT_AUTHORITY is advisable to be the name of the package
    public  static final String CONTENT_AUTHORITY= "itreverie.weatherapp";
    public  static final String PATH_WEATHER= "weather";
    public  static final String PATH_LOCATION= "location";
    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    //BASE_CONTENT_URI is the BASE of all Uri's which apps will use to contact the content provider
    public  static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);


    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    /* Inner class that defines the table contents of the weather table
    *
    * CREATE TABLE weather( _id INTEGER PRIMARY KEY,
    *                       date TEXT NOT NULL,
    *                       min REAL NOT NULL,
    *                       max REAL NOT NULL,
    *                       humidity REAL NOT NULL,
    *                       pressure REAL NOT NULL);
    *
    * */
    public static final class WeatherEntry implements BaseColumns {

        //BASE URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        //Adding the Content Provider to our Contract - add URI builders and decoders for WeatherEntry
        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        //These are the types used in our content provider
        //The specific namespace is used to identify the type to be returned
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +      PATH_WEATHER;




        //TABLE NAMES AND COLUMNS
        public static final String TABLE_NAME = "weather";
        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";






        //LOCATION
        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        //These are helper functions to decode Uri structure in this way we keep all the uri's in one place (the contract)
        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }


        //DATE
        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }
        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }


        //START DATE
        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }
        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }





    public static final class LocationEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        //Adding the Content Provider to our Contract - add URI builders and decoders for WeatherEntry
        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        //These are the MIMETYPES used in our content provider
        public static final String CONTENT_TYPE ="vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;


        public static final String TABLE_NAME = "location";
        public static final String COLUMN_LOCATION_SETTINGS = "location_settings";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_CORD_LATITUDE =  "cord_latitude";
        public static final String COLUMN_CORD_LONGITUDE = "cord_longitude";

    }
}


