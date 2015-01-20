package com.gabrielgatu.haladin.providers;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.gabrielgatu.haladin.beans.DataFlow;
import com.gabrielgatu.haladin.beans.Measure;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by gabrielgatu on 19/01/15.
 */
public class MeasuresDownloader {

    public interface DataFlowDownloaderListener {
        public void onDownloadFinished(ArrayList<Measure> measures);
    }

    private static final String BASE_URL = "http://api.smartdatanet.it/odata/SmartDataOdataService.svc/";
    private static final String URL_1 = "/Measures/?";
    private  static final String EXTRA_BASE_PARAMS = "&$select=time,value";

    private static final String TAG_DOCUMENT = "feed";
    private static final String TAG_RESULT_PROPERTY_TIME = "time";
    private static final String TAG_RESULT_PROPERTY_VALUE = "value";

    /**
     * Use the listener to communicate with the parent class
     * and to pass the data downloaded.
     */
    private DataFlowDownloaderListener mListener;


    public MeasuresDownloader(Activity activity, String apiName, String numOfResults, String orderBy, String method) {
        try {
            mListener = (DataFlowDownloaderListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Error: the activity must implement the DataFlowDownloaderListener listener.");
        }

        String url = BASE_URL + apiName + URL_1 +
                                "$top=" + numOfResults +
                                "&$orderby=" + orderBy + "%20" +
                                method +
                                EXTRA_BASE_PARAMS;
        new DataFlowAsyncTask().execute(url);
    }


    private class DataFlowAsyncTask extends AsyncTask<String, Void, ArrayList<Measure>> {

        @Override
        protected ArrayList<Measure> doInBackground(String... params) {
            String url = params[0];
            ArrayList<Measure> measures = new ArrayList<>();

            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);

                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(getUrlData(url)));

                beginDocument(parser, TAG_DOCUMENT);

                int eventType = parser.getEventType();

                String time = "";
                String value = "";
                int progressStatus = 0;

                do {
                    nextElement(parser);

                    eventType = parser.getEventType();
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();

                        if (tagName.equals(TAG_RESULT_PROPERTY_TIME) ||
                            tagName.equals(TAG_RESULT_PROPERTY_VALUE)) {

                            parser.next();
                            eventType = parser.getEventType();

                            if(eventType == XmlPullParser.TEXT){
                                String valueFromXML = parser.getText();

                                if (progressStatus == 0) time = valueFromXML;
                                if (progressStatus == 1) value = valueFromXML;

                                progressStatus++;
                            }
                        }
                    }

                    if (progressStatus == 2) {
                        progressStatus = 0;
                        measures.add(new Measure(time, value));
                    }

                } while (eventType != XmlPullParser.END_DOCUMENT);

            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            return measures;
        }

        @Override
        protected void onPostExecute(ArrayList<Measure> measures) {
            super.onPostExecute(measures);
            mListener.onDownloadFinished(measures);
        }

        public InputStream getUrlData(String url) throws URISyntaxException, IOException {

            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet method = new HttpGet(new URI(url));
            HttpResponse res = client.execute(method);

            return res.getEntity().getContent();
        }

        public final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
            int type;

            while ((type = parser.next()) != parser.START_TAG
                    && type != parser.END_DOCUMENT) {
                ;
            }

            if (type != parser.START_TAG) {
                throw new XmlPullParserException("No start tag found");
            }

            if (!parser.getName().equals(firstElementName)) {
                throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() +
                        ", expected " + firstElementName);
            }
        }

        public final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
            int type;

            while ((type = parser.next()) != parser.START_TAG
                    && type != parser.END_DOCUMENT) {
                ;
            }
        }
    }
}
