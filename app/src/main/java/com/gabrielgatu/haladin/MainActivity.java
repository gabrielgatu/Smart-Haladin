package com.gabrielgatu.haladin;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.gabrielgatu.haladin.adapters.FlowsListAdapter;
import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.Measure;
import com.gabrielgatu.haladin.providers.DataProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melnykov.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String KEY_INDEX_DATA_FLOW = "com.gabrielgatu.haladin.INDEX_DATA_FLOW";

    /**
     * Custom title of the actionbar.
     * Use it to set a custom title to the activity.
     */
    private TextView mActionBarTitle;


    /**
     * List with all the flows available.
     */
    private GridView mFlowsList;


    /**
     * The search button, when clicked open
     * a new activity where the user can perform
     * a search of the flows available inside the app.
     */
    private FloatingActionButton mFab;


    /**
     * The data flows representing, each one,
     * a specific flow available on smartdatanet.it
     */
    private ArrayList<DataFlow> mDataFlows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initActionBar();

        // Initialize UI components
        mFlowsList = (GridView) findViewById(R.id.list_flows);
        mFab = (FloatingActionButton) findViewById(R.id.fab);

        // Attach listeners
        mFab.setOnClickListener(this);

        // Init components
        initDataFlows();
        initFlowsList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDataFlows();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void initActionBar() {
        // Setup the custom actionbar and take reference of the
        // title
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        mActionBarTitle = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionbar_title);
        mActionBarTitle.setText("Haladin's Flows");
    }

    private void initDataFlows() {
        mDataFlows = DataProvider.getData(this, null);
        if (mDataFlows == null) {
            startSetupDefaultData();
        }
    }

    private void initFlowsList() {
        FlowsListAdapter adapter = new FlowsListAdapter(this, mDataFlows);
        mFlowsList.setAdapter(adapter);
        mFlowsList.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(mFlowsList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDataFlows.get(position).getMeasures() != null &&
            mDataFlows.get(position).getMeasures().size() > 0) {
                Bundle options = new Bundle();
                options.putInt(KEY_INDEX_DATA_FLOW, position);

                Intent intent = new Intent(this, DataFlowActivity.class);
                intent.putExtras(options);
                startActivity(intent);
        }
        else {
            Bundle options = new Bundle();
            options.putInt(KEY_INDEX_DATA_FLOW, position);

            Intent intent = new Intent(this, NewFlowActivity.class);
            intent.putExtras(options);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fab:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void startSetupDefaultData() {
        String json = loadJSONFromAsset();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<DataFlow>>(){}.getType();

        mDataFlows = gson.fromJson(json, type);
        DataProvider.saveData(this, mDataFlows);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getResources().openRawResource(R.raw.dataflows);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
