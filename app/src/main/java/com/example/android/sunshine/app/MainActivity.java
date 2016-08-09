package com.example.android.sunshine.app;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ArrayList<String> fake_data = new ArrayList<String>() ;
            fake_data.add("Today-Sunny-88/63");
            fake_data.add("Tommorow-Foggy-70/46");
            fake_data.add("Wednesday-Rainy-90/90");
            fake_data.add("Thursday-Rainy-90/90");
            fake_data.add("Friday-Rainy-90/90");
            fake_data.add("Saturday-Rainy-90/90");

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    //the context (this, fragments parent activity)
                    getActivity(),
                    // ID of list item layout
                    R.layout.list_item_forecast,
                    //ID of  textview to fill up the data
                    R.id.list_item_forecast_textview,
                    //fake data to be populated
                    fake_data);

            return rootView;
        }



    }

}
