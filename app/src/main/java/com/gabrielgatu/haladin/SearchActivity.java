package com.gabrielgatu.haladin;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.gabrielgatu.haladin.adapters.FlowsListAdapter;
import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.Measure;
import com.gabrielgatu.haladin.providers.DataProvider;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;


public class SearchActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, TextWatcher {

    /**
     * List with all the flows available.
     */
    private GridView mFlowsList;


    /**
     * The adapter of the flows list.
     * Use it to refresh the childs inside it,
     * based on the search field input.
     */
    private FlowsListAdapter mAdapter;


    /**
     * Take the input of the user and use it to
     * filter all the flows by their title
     * and their place.
     * Remove those who don't respect this rule.
     */
    private EditText mSearchField;


    /**
     * The data flows representing, each one,
     * a specific flow available on smartdatanet.it
     */
    private ArrayList<DataFlow> mDataFlows;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize UI components
        mFlowsList = (GridView) findViewById(R.id.list_flows);
        mSearchField = (EditText) findViewById(R.id.search_field);

        // Attach listeners
        mSearchField.addTextChangedListener(this);

        // Init components
        initDataFlows();
        initFlowsList();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initDataFlows() {
        mDataFlows = DataProvider.getData(this, null);
    }

    private void initFlowsList() {
        mAdapter = new FlowsListAdapter(this, mDataFlows);
        mFlowsList.setAdapter(mAdapter);
        mFlowsList.setOnItemClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ArrayList<DataFlow> newFlow = new ArrayList<>();

        for (DataFlow flow : mDataFlows) {
            if (flow.getName().toLowerCase().contains(s) ||
                flow.getPlace().toLowerCase().contains(s)) {
                newFlow.add(flow);
            }
        }

        mAdapter = new FlowsListAdapter(this, newFlow);
        mFlowsList.setAdapter(mAdapter);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mDataFlows.get(position).getMeasures() != null &&
                mDataFlows.get(position).getMeasures().size() > 0) {
            Bundle options = new Bundle();
            options.putInt(MainActivity.KEY_INDEX_DATA_FLOW, position);

            Intent intent = new Intent(this, DataFlowActivity.class);
            intent.putExtras(options);
            startActivity(intent);
        }
        else {
            Bundle options = new Bundle();
            options.putInt(MainActivity.KEY_INDEX_DATA_FLOW, position);

            Intent intent = new Intent(this, NewFlowActivity.class);
            intent.putExtras(options);
            startActivity(intent);
        }
    }
}
