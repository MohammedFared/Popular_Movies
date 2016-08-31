package com.example.fareed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.popularmovies.Models.MovieBean;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MoviesGrid extends Fragment implements AdapterView.OnItemClickListener{

    @Override
    public void onSaveInstanceState(Bundle outState) {
        index = moviesGrid.getFirstVisiblePosition();
        outState.putInt("index", index);
        outState.putStringArrayList("posters", posters);
        outState.putStringArrayList("date", date);
        outState.putStringArrayList("title", title);
        outState.putStringArrayList("overView", overView);
        outState.putIntegerArrayList("movieId", movieId);

        outState.putInt("flag", flag);
        outState.putInt("flagLoadMoreData", flagLoadMoreData);
        Log.d(TAG, "onSaveInstanceState: restore "+index);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

    }

    public MoviesGrid() {
        // Required empty public constructor
    }

    int flag, flagLoadMoreData, index;
    Context mContext = getContext();
    String TAG = "MOVIESGRIDLOG";
    SwipeRefreshLayout swipeContainer;
    Gson gson;
    MovieBean movieBean;
    ArrayList<String> posters = new ArrayList<>(), title = new ArrayList<>(), date = new ArrayList<>(), overView = new ArrayList<>();
    ArrayList<Integer> movieId = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    private String url;
    TextView textView;
    MovieAdapter movieAdapter;
    GridView moviesGrid;
    private int page;
    ProgressBar progressBar;
    String APIKEY = "api_key="+BuildConfig.MOVIE_API_KEY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "Fragment is here", Toast.LENGTH_SHORT).show();
        if (savedInstanceState!= null) {
            index = savedInstanceState.getInt("index", 3);
            flag = savedInstanceState.getInt("flag");
            flagLoadMoreData = savedInstanceState.getInt("flagLoadMoreData");
            posters = savedInstanceState.getStringArrayList("posters");
            date = savedInstanceState.getStringArrayList("date");
            title = savedInstanceState.getStringArrayList("title");
            overView = savedInstanceState.getStringArrayList("overView");
            movieId = savedInstanceState.getIntegerArrayList("movieId");

            Log.d(TAG, "onCreate index: " + index);
//        url = "http://api.themoviedb.org/3/movie/popular?page=1&api_key=3a624f843f50800c01512368404f203e";
//        filterList(url);
            movieAdapter = new MovieAdapter(getContext(), posters, title);
            moviesGrid.setAdapter(movieAdapter);
            moviesGrid.setOnScrollListener(new EndlessScrollListener(6, page) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
                    loadMoreData(page);
                    // or customLoadMoreDataFromApi(totalItemsCount);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
            progressBar.setVisibility(View.GONE);
            moviesGrid.setSelection(index);
        }
    }

    private void filterList(String url) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        swipeContainer.setRefreshing(true);
        Log.d(TAG+"log", "filterList: "+url);
        asyncHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                gson = new Gson();
                movieBean = gson.fromJson(new String(responseBody), MovieBean.class);
                List<MovieBean.ResultsBean> resultsList = movieBean.getResults();
                for (MovieBean.ResultsBean result : resultsList) {
                    posters.add("https://image.tmdb.org/t/p/w342" + result.getPoster_path());
                    movieId.add(result.getId());
                    title.add(result.getTitle());
                    overView.add(result.getOverview());
                    date.add(result.getRelease_date());
                    rating.add(result.getVote_average());

                    movieAdapter = new MovieAdapter(getContext(), posters, title);
                    moviesGrid.setAdapter(movieAdapter);
                    swipeContainer.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                }
//                Log.d("MainActivity", "onSuccess: Posters: "+ Arrays.toString(posters));

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
        this.page= page;
        swipeContainer.setRefreshing(true);
        if (flagLoadMoreData == 1)
            url = "http://api.themoviedb.org/3/movie/popular?page=" + page + "&" + APIKEY;
        else if (flagLoadMoreData == 0)
            url = "http://api.themoviedb.org/3/movie/top_rated?page=" + page + "&" + APIKEY;

//        Log.d("MAINACTIVITY", "loadMoreData: "+url);
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(mContext, url, new AsyncHttpResponseHandler() {
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
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        moviesGrid = (GridView) view.findViewById(R.id.moviesGrid);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView = (TextView) view.findViewById(R.id.textView_popOrTop);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        if (savedInstanceState == null){
            moviesGrid.setOnScrollListener(new com.example.fareed.popularmovies.EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
                    loadMoreData(page);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
            swipeContainer.setRefreshing(true);
            Log.d(TAG, "onCreate: restore");
            flag = 0;
            url = "http://api.themoviedb.org/3/movie/popular?page=1&"+APIKEY;
            progressBar.setVisibility(View.VISIBLE);
            filterList(url);
            swipeContainer.setRefreshing(false);
        }

        moviesGrid.setOnItemClickListener(this);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
            }
        });
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(TAG, "onItemClick: "+position);
        Intent intent = new Intent(getContext(), MovieDetails.class);
        intent.putExtra("title", title.get(position));
        intent.putExtra("date", date.get(position));
        intent.putExtra("poster", "https://image.tmdb.org/t/p/w185" + posters.get(position));
//            intent.putExtra("rating", rating.get(position));
        intent.putExtra("overView", overView.get(position));
        intent.putExtra("movieId", movieId.get(position));
        startActivity(intent);
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
                url = "http://api.themoviedb.org/3/movie/popular?page=1&"+APIKEY;
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
                url = "http://api.themoviedb.org/3/movie/top_rated?page=1&"+APIKEY;
                Log.d(TAG, "onCreate: "+url);
                textView.setText("Top rated");
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menuFilter);
        Drawable drawable = menuItem.getIcon();
        if (drawable != null) {
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
}
