package com.example.albertsnewsapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;
    private static final String USGS_REQUEST_URL =
            "http://content.guardianapis.com/search?q=debates&api-key=test";

    private static final int News_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                News currentNews = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(News_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText("no_internet_connection");
        }

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);
    }
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText("No news");
        mAdapter.clear();
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }
}
