package com.gabrielgatu.haladin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.InfoBox;
import com.gabrielgatu.haladin.beans.Measure;
import com.gabrielgatu.haladin.fragments.DataFlowInfoBoxFragment;
import com.gabrielgatu.haladin.providers.DataProvider;
import com.gabrielgatu.haladin.providers.MeasuresDownloader;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.YLabels;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class DataFlowActivity extends ActionBarActivity implements MeasuresDownloader.DataFlowDownloaderListener {

    /**
     * A viewpager containing the extra infos below the
     * chart. It will contain 2 pages:
     * 1 - Main infos, as: average value, interval of time,
     *                     maximum value, minimum value
     * 2 - Extra infos, as: first value, last, value,
     *                      median value, total number of values
     */
    private ViewPager mPager;


    /**
     * The chart rappresenting all the values.
     */
    private LineChart mChart;


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
     * Use it for the chart and the settings of the extra info panels.
     */
    private int mIndexDataFlowSelected = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_flow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Recover data from bundle
        if (getIntent().getExtras() != null) {
            mIndexDataFlowSelected = getIntent().getExtras().getInt(MainActivity.KEY_INDEX_DATA_FLOW);
        }

        // Initialize UI components
        mChart = (LineChart) findViewById(R.id.chart);
        mPager = (ViewPager) findViewById(R.id.pager);
        mProgressBar = (SmoothProgressBar) findViewById(R.id.progress_bar);

        // Initializations
        initDataFlows();
        initViewPager();

        // Set the title of the actionbar to be the name of the dataflow
        String title = mDataFlows.get(mIndexDataFlowSelected).getName();
        String titleCapitalized = Character.toUpperCase(title.charAt(0)) + title.substring(1);
        getSupportActionBar().setTitle(titleCapitalized);

        // Setup the chart
        initChart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_flow, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.action_refresh_flow:
                startUpdatingData();
                return true;
            case R.id.action_delete_flow:
                deleteDataFlow();
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

    private void initChart() {
        String title = mDataFlows.get(mIndexDataFlowSelected).getName();
        String titleCapitalized = Character.toUpperCase(title.charAt(0)) + title.substring(1);

        LineData data1 = getData(titleCapitalized);
        setupChart(mChart, data1, getResources().getColor(R.color.default_secondary));
    }

    private void setupChart(LineChart chart, LineData data, int color) {

        // if enabled, the chart will always start at zero on the y-axis
        chart.setStartAtZero(true);

        // disable the drawing of values into the chart
        chart.setDrawYValues(false);

        chart.setDrawBorder(false);

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable / disable grid lines
        chart.setDrawVerticalGrid(false);
        // mChart.setDrawHorizontalGrid(false);
        //
        // enable / disable grid background
        chart.setDrawGridBackground(false);
        chart.setGridColor(Color.WHITE & 0x70FFFFFF);
        chart.setGridWidth(1.25f);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setBackgroundColor(color);

        // add data
        chart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = chart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(6f);
        l.setTextColor(Color.WHITE);

        YLabels y = chart.getYLabels();
        y.setTextColor(Color.WHITE);
        y.setLabelCount(4);

        XLabels x = chart.getXLabels();
        x.setTextColor(Color.WHITE);
        x.setPosition(XLabels.XLabelPosition.BOTTOM);

        // animate calls invalidate()...
        chart.animateX(2500);
    }

    private LineData getData(String title) {
        DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);

        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0, length = flow.getMeasures().size(); i < length; i++) {
            xVals.add("x: " + i);
        }

        ArrayList<Entry> yVals = new ArrayList<>();
        for (int i = 0, length = flow.getMeasures().size(); i < length; i++) {
            String measure = flow.getMeasures().get(i).getValue();
            float val = Float.parseFloat(measure);
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, title);

        set1.setLineWidth(1.75f);
        set1.setCircleSize(3f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setHighLightColor(Color.WHITE);

        ArrayList<LineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(xVals, dataSets);
        return data;
    }

    private void initViewPager() {
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(adapter);
    }

    private void deleteDataFlow() {

        // Remove all the measures
        DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);
        flow.setMeasures(new ArrayList<Measure>());

        // Save the changes
        DataProvider.saveData(this, mDataFlows);

        // Go to the main activity
        this.finish();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private void startUpdatingData() {

        if (isNetworkConnected()) {
            DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);

            String numOfResults = flow.getMeasures().size() + "";
            String orderBy = flow.getOrderedBy();
            String method = flow.getMethodUsed();

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

        // Hide the progressbar
        mProgressBar.setVisibility(View.INVISIBLE);

        // Refresh the UI
        initChart();
        initViewPager();

        // Save data
        DataProvider.saveData(this, mDataFlows);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            DataFlow flow = mDataFlows.get(mIndexDataFlowSelected);

            switch(position) {
                case 0:
                    return DataFlowInfoBoxFragment.newInstance(new InfoBox[]{
                            new InfoBox("average", getAverage(flow) + flow.getMeasureType()),
                            new InfoBox("total results", flow.getMeasures().size() + ""),
                            new InfoBox("maximum", getMaximum(flow) + flow.getMeasureType()),
                            new InfoBox("minimum", getMinimum(flow) + flow.getMeasureType())
                    });
                case 1:
                    return DataFlowInfoBoxFragment.newInstance(new InfoBox[]{
                            new InfoBox("first value", flow.getMeasures().get(0).getValue() + flow.getMeasureType()),
                            new InfoBox("last value", getLowestValue(flow) + flow.getMeasureType()),
                            new InfoBox("middle value", getMiddleValue(flow) + flow.getMeasureType()),
                            new InfoBox("total time elapsed", getTimeElapsed(flow) + " m")
                    });
                default:
                    return null;
            }
        }

        private String getAverage(DataFlow flow) {
            float total = 0;
            for (Measure measure : flow.getMeasures()) {
                total += Float.parseFloat( measure.getValue() );
            }
            return String.format("%.1f", total / flow.getMeasures().size());
        }

        private String getMaximum(DataFlow flow) {
            float maximum = Float.parseFloat( flow.getMeasures().get(0).getValue() );
            for (Measure measure : flow.getMeasures()) {
                float value = Float.parseFloat(measure.getValue());
                if (value >= maximum) {
                    maximum = value;
                }
            }
            return maximum + "";
        }

        private String getMinimum(DataFlow flow) {
            float minimum = Float.parseFloat( flow.getMeasures().get(0).getValue() );
            for (Measure measure : flow.getMeasures()) {
                float value = Float.parseFloat(measure.getValue());
                if (value <= minimum) {
                    minimum = value;
                }
            }
            return minimum + "";
        }

        private String getLowestValue(DataFlow flow) {
            return flow.getMeasures().get(flow.getMeasures().size() - 1).getValue();
        }

        private String getMiddleValue(DataFlow flow) {
            return flow.getMeasures().get((int) flow.getMeasures().size() / 2).getValue();
        }

        private String getTimeElapsed(DataFlow flow) {
            double seconds = ( 1 / flow.getFps() );
            return String.format("%.1f", flow.getMeasures().size() * (seconds / 60));
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
