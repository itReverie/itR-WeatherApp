package itreverie.weatherapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import itreverie.weatherapp.data.WeatherContract;


/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */
public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static final int COL_WEATHER_ID = 0;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;


    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout=true;

    public void setUseTodayLayout(boolean useTodayLayout)
    {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0  && mUseTodayLayout)? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;//There will eb two type of rendering
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        }
        else if (viewType == VIEW_TYPE_FUTURE_DAY)
        {
            layoutId = R.layout.list_item_forecast;
        }


        View view= LayoutInflater.from(context).inflate(layoutId,parent,false);
        //By associating the view holder with the view via the tag we help the application to perform faster and avoid findviews() calls
        ViewHolder viewHolder= new ViewHolder(view);
        //Set tag would be like session as it saves the state of certain objects but do not abusse it as you need to know what you keep in there.
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //No I need to read from the viewHodler
        ViewHolder viewHolder= (ViewHolder) view.getTag();


        //This item determines the position of an item in the list
        int viewType=getItemViewType(cursor.getPosition());
        //WeatherContract.WeatherEntry.
        //int weatherId = cursor.getInt(COL_WEATHER_ID);
        //Option 2
        //int weatherId = cursor.getInt(main_fragment.COL_WEATHER_ID);
        //Option 1
        int weatherId= cursor.getInt(cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID));
        switch (viewType)
        {
            //GETTING ART RESOURCE (color image)
            case VIEW_TYPE_TODAY:
            {
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
                break;
            }
            //GETTING ICON CONDITION (gray image)
            case VIEW_TYPE_FUTURE_DAY:
            {
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
                break;
            }
        }

        // Read date from cursor
        String dateString = cursor.getString(main_fragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        //TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        String test= Utility.getFriendlyDayString(context, dateString);
        viewHolder.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(main_fragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        //TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        viewHolder.descriptionView.setText(description);


        // For accessibility, add a content description to the icon field
        viewHolder.iconView.setContentDescription(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(main_fragment.COL_WEATHER_MAX_TEMP);
        // Find TextView and set weather forecast on it
        //TextView highTemperatureView = (TextView) view.findViewById(R.id.list_item_high_textview);
        viewHolder.highTempView.setText(Utility.formatTemperature(context,high));

        // Read low temperature from cursor
        float low = cursor.getFloat(main_fragment.COL_WEATHER_MIN_TEMP);
        // Find TextView and set weather forecast on it
        //TextView lowTemperatureView = (TextView) view.findViewById(R.id.list_item_low_textview);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context,low));

    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}