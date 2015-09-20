package com.gmail.micsha123.farposttestapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.gmail.micsha123.farposttestapp.R;
import com.gmail.micsha123.farposttestapp.data.Links;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/** Service for HTTP connection, getting and parsing HTML */
public class LoadService extends IntentService {

    /** Status for running service */
    public static final int STATUS_RUNNING = 0;
    /** Status for finished service */
    public static final int STATUS_FINISHED = 1;
    /** Status for error */
    public static final int STATUS_ERROR = 2;

    private static final String TAG = "LoadService";

    public LoadService() {
        super(LoadService.class.getName());
    }
    /** List of extracted links */
    private ArrayList<String> links = new ArrayList<String>();

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "Service Started!");

        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String url = intent.getStringExtra("url");

        Bundle bundle = new Bundle();

        /** Sending statuses to receiver of Fragment */
        if (!TextUtils.isEmpty(url)) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                ArrayList<String> results = downloadDataToDB(url);
//                if (results != null && results.size() > 0) {
                    bundle.putInt("result", results.size());
                    receiver.send(STATUS_FINISHED, bundle);
//                }
            } catch (Exception e) {
                /** Handling exception and returning text depend on error*/
                if(e instanceof HttpStatusException) {
                    switch (((HttpStatusException) e).getStatusCode()){
                        case 404:
                            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_status_404));
                            break;
                        case 403:
                            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_status_403));
                            break;
                        default:
                            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_status));
                    }
                } else if(e instanceof UnknownHostException){
                    bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_host));
                } else if(e instanceof SocketTimeoutException){
                    bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_timeout));
                } else if(e instanceof UnsupportedMimeTypeException){
                    bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.e_unsupported));
                }
                receiver.send(STATUS_ERROR, bundle);
            }
        }
        Log.d(TAG, "Service Stopping!");
        this.stopSelf();
    }

    /** Method provides making HTTP requests and downloading HTML for parsing and saving to database */
    private ArrayList<String> downloadDataToDB(String requestUrl) throws IOException {
        /** Avoiding Malformed exception */
        if(!requestUrl.startsWith("http://")){
            requestUrl = "http://" + requestUrl;
        }

        Connection connection = Jsoup.connect(requestUrl).timeout(10000);
        Connection.Response response = connection.execute();
        int statusCode = response.statusCode();

        if (statusCode == 200) {
            /** Parsing html */
            Document doc = connection.get();
            Elements links = doc.select("a[href]");
            for (Element link : links) {
                this.links.add(link.attr("abs:href"));
            }
            /** Getting Links instance for managing database*/
            Links requestsInstance = Links.getInstance(this);
            requestsInstance.saveLinksToDB(this.links);
            requestsInstance.loadLinksFromDB();
            return this.links;
        } else{
            return null;
        }
    }

}
