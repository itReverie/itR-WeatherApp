package itreverie.weatherapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import itreverie.Processing.WeatherDataParser;


public class main_activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.mainContainer, new Main_Fragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_settings, menu);
        return  super.onCreateOptionsMenu(menu);
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
    public static class Main_Fragment extends Fragment {

        public ArrayAdapter<String> arrayAdapterForecast;
        List<String> listStringForecast =  new ArrayList<String>();
        ListView listView=null;
        FetchWeatherTask weatherTask= new FetchWeatherTask();
        String[] listResultWeather = new String[0];
        SharedPreferences sharedPref =null;

        //CONSTRUCTOR
        public Main_Fragment() {
        }



        //EXTRA BUTTON "REFRESH"
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            //super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.mobileservice, menu);
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if(id == R.id.action_mobileservice)
            {
                CallAzureMobileService();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
        public void CallAzureMobileService()
        {
            /*
            try {
                mClient = new MobileServiceClient(Constants.MOBILE_SERVICE_URL_JS,
                        Constants.MOBILE_SERVICE_APPLICATION_KEY_JS, this);

                dk.com.unaaplicacion.Distance distance = new dk.com.unaaplicacion.Distance();
                distance.Name="100";
                distance.Unit = "mts";

                mClient.getTable(dk.com.unaaplicacion.Distance.class).insert(distance, new TableOperationCallback<dk.com.unaaplicacion.Distance>() {
                    public void onCompleted(dk.com.unaaplicacion.Distance entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            Log.v(LOG_TAG, "BUILD URI " + "SUCCESSFULL ");
                        } else {
                            String msg = exception.getCause().getMessage();
                            Log.v(LOG_TAG, "FAIL "+msg);
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(LOG_TAG, "BUILD URI " + e.getMessage());
            }
            */
        }




        //LIST ITEMS IN THE MAIN LAYOUT
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            //View rootView = inflater.inflate(R.layout.main_fragment, container, false);
            //return rootView;

            sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());//where this is the context
            String locationDefault = sharedPref.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));

            //1851632
            //2172797
            //GETTING THE INFORMATION FROM THE BACK END
            weatherTask= new FetchWeatherTask();
            listResultWeather = new String[0];
            try {
                listResultWeather = weatherTask.execute(locationDefault).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }


            //SETTING THE INFORMATION FOR THE ITEM DETAILS
            View rootView = inflater.inflate(R.layout.main_fragment, container, false);
            listStringForecast = new ArrayList<String>(Arrays.asList(listResultWeather));
            arrayAdapterForecast = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, listStringForecast);
            listView= ((ListView) rootView.findViewById(R.id.list_view_forecast));
            listView.setAdapter(arrayAdapterForecast);


            //Adding the listener to CLICK IN THE ITEM
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Context context = getActivity();//parent.getContext();
                    String text = arrayAdapterForecast.getItem(position);

                    Intent intent= new Intent(getActivity(),detail_activity.class).putExtra(Intent.EXTRA_TEXT,text);
                    startActivity(intent);

                    //This is the code to have a toast (pop up message)
                    //int duration = Toast.LENGTH_SHORT;
                    //Toast toast = Toast.makeText(context, text, duration);
                    //toast.show();
                }
            });


            return rootView;
        }

        /**
         * Asynchronous Task to interact with the back end.
         */
        public class FetchWeatherTask extends AsyncTask<String, Void, String[]>
        {
            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();


            @Override
            protected String[] doInBackground(String... params) {

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?id="+locationID+"&mode="+mode+"&units="+units+"&cnt="+frequency);
                String QUERY_PARAM =  "id";
                String FORMAT_PARAM =  "mode";
                String UNITS_PARAM =  "units";
                String DAYS_PARAM =  "cnt";

                String param_query = params[0].toString();
                String param_format = "json";// params[1].toString();
                String param_units = "metric";// params[2].toString();
                int param_numdays = 10;// params[3].toString();

                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast


                    String FORECAST_BASE_URL="http://api.openweathermap.org/data/2.5/forecast/daily?";
                    Uri urlBuilder2 = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM, param_query)
                            .appendQueryParameter(FORMAT_PARAM, param_format)
                            .appendQueryParameter(UNITS_PARAM, param_units)
                            .appendQueryParameter(DAYS_PARAM, String.valueOf(param_numdays))
                            .build();
                    Log.v(LOG_TAG, "BUILD URI " + urlBuilder2.toString());

                    // Create the request to OpenWeatherMap, and open the connection
                    URL url = new URL(urlBuilder2.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        forecastJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        forecastJsonStr = null;
                    }
                    forecastJsonStr = buffer.toString();
                    Log.v(LOG_TAG,"Forecast JSON String: "+forecastJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    forecastJsonStr = null;
                }  finally{
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }


                try{

                    WeatherDataParser test=new WeatherDataParser();
                    String[] weatherDataArray= test.getWeatherDataFromJson(forecastJsonStr, param_numdays);

                    return weatherDataArray;
                }
                catch(JSONException ex)
                {
                    Log.e(LOG_TAG, ex.getMessage(),ex);
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String[] result)
            {
                if(result!= null)
                {
                    arrayAdapterForecast.clear();
                    for(String dayForecasterStr: result)
                    {
                        arrayAdapterForecast.add(dayForecasterStr);
                    }
                }
            }
        }
    }
}
