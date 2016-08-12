package com.example.android.sunshine.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.text.format.Time;
import android.widget.Toast;
import android.content.Intent ;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rupesh on 8/9/16.
 */

    /**
     * A placeholder fragment containing a simple view.
     */
    public  class ForecastFragment extends Fragment {
        ArrayAdapter<String> arrayAdapter ;
        public ForecastFragment() {
        }
        ////////////////
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // Add this line in order for this fragment to handle menu events.
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.forecastfragment, menu);
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_refresh) {
                FetchWeatherTask weatherTask = new FetchWeatherTask();
                weatherTask.execute("14214,ny"); // passing the postal code as the parameter
                return true;
            }


            return super.onOptionsItemSelected(item);
        }




        /////////////////
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ////////////////////////////////////////////////////////////////////////

            //////////////////////////////////////////////////////////////////
            ArrayList<String> fake_data = new ArrayList<String>() ;
            fake_data.add("Today-Sunny-88/63");
            fake_data.add("Tommorow-Foggy-70/46");
            fake_data.add("Wednesday-Rainy-90/90");
            fake_data.add("Thursday-Rainy-90/90");
            fake_data.add("Friday-Rainy-90/90");
            fake_data.add("Saturday-Rainy-90/90");

            // minor experimental change getActivity changed to getContext

             arrayAdapter = new ArrayAdapter<String>(
                    //the context (this, fragments parent activity)
                    getContext(),
                    // ID of list item layout
                    R.layout.list_item_forecast,
                    //ID of  textview to fill up the data
                    R.id.list_item_forecast_textview,
                    //fake data to be populated
                    fake_data);

            ListView listView = (ListView) rootView.findViewById(R.id.list_view_forecast);
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> adapterView,View view ,int i,long l){
                    //Context context = getContext();
                    //CharSequence text = arrayAdapter.getItem(i);
                    //int duration = Toast.LENGTH_SHORT;

                    //Toast toast = Toast.makeText(context, text, duration);
                    //toast.show();
                    String text = arrayAdapter.getItem(i) ;
                    Intent detail = new Intent(getActivity(),DetailedActivity.class).putExtra(Intent.EXTRA_TEXT,text) ;
                    startActivity(detail);
                }
            });

            return rootView;
        }

        /////////////////////////


        public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
////////////////////////
            private String getReadableDateString(long time){
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(time);
            }

            private String formatHighLows(double high, double low) {
                // For presentation, assume the user doesn't care about tenths of a degree.
                long roundedHigh = Math.round(high);
                long roundedLow = Math.round(low);

                String highLowStr = roundedHigh + "/" + roundedLow;
                return highLowStr;
            }


            private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                            throws JSONException

            {

                                    // These are the names of the JSON objects that need to be extracted.
                 final String OWM_LIST = "list";
                 final String OWM_WEATHER = "weather";
                 final String OWM_TEMPERATURE = "temp";
                 final String OWM_MAX = "max";
                 final String OWM_MIN = "min";
                 final String OWM_DESCRIPTION = "description";
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                Time dayTime = new Time();
                dayTime.setToNow();

                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                dayTime = new Time();

                String[] resultStrs = new String[numDays];
                for(int i = 0; i < weatherArray.length(); i++) {
                    // For now, using the format "Day, description, hi/low"
                    String day;
                    String description;
                    String highAndLow;

                    // Get the JSON object representing the day
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    // The date/time is returned as a long.  We need to convert that
                    // into something human-readable, since most people won't read "1400356800" as
                    // "this saturday".
                    long dateTime;
                    // Cheating to convert this to UTC time, which is what we want anyhow
                    dateTime = dayTime.setJulianDay(julianStartDay+i);
                    day = getReadableDateString(dateTime);

                    // description is in a child array called "weather", which is 1 element long.
                    JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);

                    // Temperatures are in a child object called "temp".  Try not to name variables
                    // "temp" when working with temperature.  It confuses everybody.
                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                    double high = temperatureObject.getDouble(OWM_MAX);
                    double low = temperatureObject.getDouble(OWM_MIN);

                    highAndLow = formatHighLows(high, low);
                    description = toCamelCase(description);
                    resultStrs[i] = day + " - " + description + " - " + highAndLow;
                }

//                for (String s : resultStrs) {
//                    Log.v(LOG_TAG, "Forecast entry: " + s);
//                }
                return resultStrs;



            }

            private String toCamelCase(String s){
                String[] parts = s.split(" ");
                String camelCaseString = "";
                for (String part : parts){
                    camelCaseString = camelCaseString + toProperCase(part)+" ";
                }
                return camelCaseString;
            }

            private String toProperCase(String s) {
                return s.substring(0, 1).toUpperCase() +
                        s.substring(1).toLowerCase();
            }

            ////////////////////
            @Override
            protected String[] doInBackground(String... params) {

               // verify that zip code has been provided
                if(params.length == 0){
                    return null ;
                }

                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;
                String format = "json" ;
                String units = "metric" ;
                int numDays = 7 ;
                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                  //  String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                    final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                    final String QUERY_PARAM = "q";
                    final String FORMAT_PARAM = "mode" ;
                    final String UNITS_PARAM= "units" ;
                    final String DAYS_PARAM = "cnt" ;
                    final String APPID_PARAM = "APPID";

                    Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                            .appendQueryParameter(QUERY_PARAM,params[0])
                            .appendQueryParameter(FORMAT_PARAM,format)
                            .appendQueryParameter(UNITS_PARAM,units)
                            .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                            .appendQueryParameter(APPID_PARAM,BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                            .build();




                   // String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                    //URL url = new URL(baseUrl.concat(apiKey));

                    URL url = new URL(builtUri.toString()) ;

                    Log.v(LOG_TAG,"Built URI "+ builtUri.toString());
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
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
                        return null;
                    }
                    forecastJsonStr = buffer.toString();

                    Log.v(LOG_TAG,"ForeCast JSON String "+forecastJsonStr);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.

                }
                try {
                    return getWeatherDataFromJson(forecastJsonStr, numDays);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String[] strings) {
                if(strings != null){
                    arrayAdapter.clear();
                    arrayAdapter.addAll(strings);
                }
            }
        }



        /////////////////////////////

    }


