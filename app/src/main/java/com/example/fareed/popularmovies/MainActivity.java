package com.example.fareed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    int flag, flagLoadMoreData, index;
    String TAG = "MAINACTIVITY";
    Context mcontext = this;
    SwipeRefreshLayout swipeContainer;
    Gson gson;
    MovieBean movieBean;
    ArrayList<String> posters = new ArrayList<>(), title = new ArrayList<>(), date = new ArrayList<>(), overView = new ArrayList<>();
    ArrayList<Integer> movieId = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    public String url;
    TextView textView;
    MovieAdapter movieAdapter;
    GridView moviesGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        if (savedInstanceState != null){
//            index = savedInstanceState.getInt("index", 0);
//            Log.d(TAG, "indexxx: "+ index);
//            moviesGrid.setSelection(index);
//            posters = savedInstanceState.getStringArrayList("posters");
//            flag = savedInstanceState.getInt("flag");
//            flagLoadMoreData = savedInstanceState.getInt("flagLoadMoreData");
//        }
//        else {
            textView = (TextView) findViewById(R.id.textView_popOrTop);
            swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
            swipeContainer.setRefreshing(true);
        Log.d(TAG, "onCreate: restore");
            //call the first page
            flag = 0;
            url = "http://api.themoviedb.org/3/movie/popular?page=1&api_key=3a624f843f50800c01512368404f203e";
            filterList(url);
        Log.d(TAG, "onCreate: restore");
            Log.d(TAG, "onCreate: " + url);
            swipeContainer.setRefreshing(false);
//        }
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        index = savedInstanceState.getInt("index", 3);
        flag = savedInstanceState.getInt("flag");
        flagLoadMoreData = savedInstanceState.getInt("flagLoadMoreData");
        Log.d(TAG, "onRestoreInstanceState index: "+index);

        url = "http://api.themoviedb.org/3/movie/popular?page=1&api_key=3a624f843f50800c01512368404f203e";
        filterList(url);
        Log.d(TAG, "onRestoreInstanceState index: "+index);
        moviesGrid.setSelection(index);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        index = moviesGrid.getFirstVisiblePosition();
        outState.putInt("index", index);
        outState.putStringArrayList("posters", posters);
        outState.putInt("flag", flag);
        outState.putInt("flagLoadMoreData", flagLoadMoreData);
        Log.d(TAG, "onSaveInstanceState: restore "+index);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menuFilter);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //filter 'Navigate between the popular and Top Rated LISTS'
        if (item.getItemId() == R.id.menuFilter){
            if(flag == 1) {
                flag=0;
                flagLoadMoreData=1;
                posters.clear();
                movieId.clear();
                title.clear();
                overView.clear();
                date.clear();
                rating.clear();
                url = "http://api.themoviedb.org/3/movie/popular?page=1&api_key=3a624f843f50800c01512368404f203e";
                Log.d(TAG, "onCreate: "+url);
                textView.setText("Popular");
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
            else if(flag == 0) {
                flag = 1;
                flagLoadMoreData=0;
                posters.clear();
                movieId.clear();
                title.clear();
                overView.clear();
                date.clear();
                rating.clear();
                url = "http://api.themoviedb.org/3/movie/top_rated?page=1&api_key=3a624f843f50800c01512368404f203e";
                Log.d(TAG, "onCreate: "+url);
                textView.setText("Top rated");
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void filterList(String url) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        swipeContainer.setRefreshing(true);
        Log.d(TAG+"log", "filterList: "+url);
        asyncHttpClient.get(this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeContainer.setRefreshing(false);
                gson = new Gson();
                movieBean = gson.fromJson(new String(responseBody), MovieBean.class);
                List<MovieBean.ResultsBean> resultsList = movieBean.getResults();
                for (MovieBean.ResultsBean result : resultsList) {
                    posters.add("https://image.tmdb.org/t/p/w342" + result.getPoster_path());
                    Log.d(TAG, "onSuccess: restore");
                    movieId.add(result.getId());
                    title.add(result.getTitle());
                    overView.add(result.getOverview());
                    date.add(result.getRelease_date());
                    rating.add(result.getVote_average());
                }
//                Log.d("MainActivity", "onSuccess: Posters: "+ Arrays.toString(posters));
                moviesGrid = (GridView) findViewById(R.id.moviesGrid);
                Log.d(TAG, "onSuccess: restoree");
                moviesGrid.setOnScrollListener(new com.example.fareed.popularmovies.EndlessScrollListener() {
                    @Override
                    public boolean onLoadMore(int page, int totalItemsCount) {
                        // Triggered only when new data needs to be appended to the list
                        // Add whatever code is needed to append new items to your AdapterView
                        loadMoreData(page);
                        // or customLoadMoreDataFromApi(totalItemsCount);
                        return true; // ONLY if more data is actually being loaded; false otherwise.
                    }
                });
                moviesGrid.setOnItemClickListener((AdapterView.OnItemClickListener) mcontext);
                movieAdapter = new MovieAdapter(getApplicationContext(), posters, title, rating);
                moviesGrid.setAdapter(movieAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    //append more data to the adapter
    private void loadMoreData(int page) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter
        swipeContainer.setRefreshing(true);
        if (flagLoadMoreData == 1){
            url = "http://api.themoviedb.org/3/movie/popular?page=" + page + "&api_key=3a624f843f50800c01512368404f203e";}
        else if (flagLoadMoreData == 0){Log.d(TAG, "loadMoreData: "+flag+" ");
            url = "http://api.themoviedb.org/3/movie/top_rated?page=" + page + "&api_key=3a624f843f50800c01512368404f203e";}

//        Log.d("MAINACTIVITY", "loadMoreData: "+url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                swipeContainer.setRefreshing(false);
                gson = new Gson();
                movieBean = gson.fromJson(new String(responseBody), MovieBean.class);
                List<MovieBean.ResultsBean> resultsList = movieBean.getResults();

                for (MovieBean.ResultsBean result : resultsList) {
                    posters.add("https://image.tmdb.org/t/p/w342" + result.getPoster_path());
                    movieId.add(result.getId());
                    title.add(result.getOriginal_title());
                    overView.add(result.getOverview());
                    date.add(result.getRelease_date());
                    rating.add(result.getVote_average());
                }
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(TAG, "onItemClick: "+position);

            Intent intent = new Intent(this, MovieDetails.class);
            intent.putExtra("title", title.get(position));
            intent.putExtra("date", date.get(position));
            intent.putExtra("poster", "https://image.tmdb.org/t/p/w185" + posters.get(position));
            intent.putExtra("rating", rating.get(position));
            intent.putExtra("overView", overView.get(position));
            startActivity(intent);
    }
}
