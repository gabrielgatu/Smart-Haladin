package com.gabrielgatu.haladin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.Measure;
import com.gabrielgatu.haladin.providers.DataProvider;
import com.gabrielgatu.haladin.providers.MeasuresDownloader;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class NewFlowActivity extends ActionBarActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        MeasuresDownloader.DataFlowDownloaderListener {

    private static final String PARAM_TIME = "time";
    private static final String PARAM_VALUE = "value";
    private static final String PARAM_INCREASING = "asc";
    private static final String PARAM_DECREASING = "desc";

    /**
     * Indicates the number of the results for the request.
     */
    private SeekBar mNumOfResultsBar;

    /**
     * Show the value of the results bar.
     */
    private TextView mNumOfResultsText;

    /**
     * Contains 2 radio buttons, get the parameter
     * relative to them using their id.
     */
    private RadioGroup mOrderByGroup;

    /**
     * Contains 2 radio buttons, get the parameter
     * relative to them using their id.
     */
    private RadioGroup mMethodGroup;

    /**
     * When clicked, start downloading the data using
     * all the parameters specified by the user.
     */
    private FloatingActionButton mFab;

    /**
     * Show when downloading data, hide when finished.
     */
    private SmoothProgressBar mProgressBar;

    /**
     * The data flows representing, each one,
     * a specific flow available on smartdatanet.it
     */
    private ArrayList<DataFlow> mDataFlows;

    /**
     * Index of the selected dataflow inside the list.
     * Use it to refresh the correct dataflow with the data downloaded.
     */
    private int mIndexDataFlowSelected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_flow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recover data from bundle
        if (getIntent().getExtras() != null) {
            mIndexDataFlowSelected = getIntent().getExtras().getInt(MainActivity.KEY_INDEX_DATA_FLOW);
        }

        // Initialize the UI components
        mNumOfResultsBar = (SeekBar) findViewById(R.id.num_of_results_bar);
        mNumOfResultsText = (TextView) findViewById(R.id.num_of_results_text);
        mOrderByGroup = (RadioGroup) findViewById(R.id.order_by_group);
        mMethodGroup = (RadioGroup) findViewById(R.id.method_group);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        // Attach listeners
        mNumOfResultsBar.setOnSeekBarChangeListener(this);
        mFab.setOnClickListener(this);

        initDataFlows();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_flow, menu);
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

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.fab:
                startDownloadWithParams();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mNumOfResultsText.setText(progress + "");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private void startDownloadWithParams() {
        if (isNetworkConnected()) {

            String numOfResults = mNumOfResultsBar.getProgress()  + "";
            String orderBy = "";
            String method = "";

            // Get the order by option
            switch(mOrderByGroup.getCheckedRadioButtonId()) {
                case R.id.order_by_time:
                    orderBy = PARAM_TIME;
                    break;
                case R.id.order_by_value:
                    orderBy = PARAM_VALUE;
                    break;
            }

            // Get the method option
            switch(mMethodGroup.getCheckedRadioButtonId()) {
                case R.id.method_increasing:
                    method = PARAM_INCREASING;
                    break;
                case R.id.method_decreasing:
                    method = PARAM_DECREASING;
                    break;
            }

            // Register the parameters used
            DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);
            flow.setOrderedBy(orderBy);
            flow.setMethodUsed(method);

            // Show the progressbar
            mProgressBar.setVisibility(View.VISIBLE);

            // Start downloading
            new MeasuresDownloader(this, flow.getApiName(), numOfResults, orderBy, method);
        }
        else {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.internet_connection_error))
                    .setMessage(getString(R.string.internet_connection_error_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    @Override
    public void onDownloadFinished(ArrayList<Measure> measures) {

        DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);
        flow.setMeasures(measures);

        // Dismiss the progressbar
        mProgressBar.setVisibility(View.INVISIBLE);

        // Save the data
        DataProvider.saveData(this, mDataFlows);

        // Launch the detail activity
        Bundle options = new Bundle();
        options.putInt(MainActivity.KEY_INDEX_DATA_FLOW, mIndexDataFlowSelected);

        Intent intent = new Intent(getBaseContext(), DataFlowActivity.class);
        intent.putExtras(options);
        startActivity(intent);
    }
}
