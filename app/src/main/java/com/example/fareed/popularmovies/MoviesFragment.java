package com.example.fareed.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MoviesFragment extends Fragment implements AdapterView.OnItemClickListener{

    // 0 >> popular   1 >> topRated
    int flag, flagLoadMoreData = 0, index;
    Context mContext = getContext();
    public static String TAG = "MOVIESGRIDLOG";
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

    Bundle savedInstanceState;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        index = moviesGrid.getFirstVisiblePosition();
        outState.putInt("index", index);
        outState.putStringArrayList("posters", posters);
        outState.putStringArrayList("date", date);
        outState.putStringArrayList("title", title);
        outState.putStringArrayList("overView", overView);
        outState.putIntegerArrayList("movieId", movieId);

        double[] ratingArr = new double[rating.size()];
        for (int i = 0; i < ratingArr.length; i++) {
            ratingArr[i] = rating.get(i);
        }
        Log.d(TAG, "onSaveInstanceState: "+ Arrays.toString(ratingArr));
        outState.putDoubleArray("rating", ratingArr);

        outState.putInt("flag", flag);
        outState.putInt("page", page);
        outState.putInt("flagLoadMoreData", flagLoadMoreData);
        Log.d(TAG, "onSaveInstanceState: restore "+index);
        super.onSaveInstanceState(outState);
    }

    public MoviesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("POP Movies");
        super.onStart();
        if (!isOnline()){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activty, new NoInternetFragment())
                    .commit();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        //if tablet mode
        if (getArguments() != null) {
            if (getArguments().getBoolean("masterDetail")) {
                //TODO: Add the first movie to the detail fragment
            }
        }
//        if (!isOnline()){
//            getActivity().getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_activty, new NoInternetFragment())
//                    .commit();
//        }

        if (savedInstanceState!= null) {
            Toast.makeText(getContext(), "Fragment is here", Toast.LENGTH_SHORT).show();
            index = savedInstanceState.getInt("index", 3);
            flag = savedInstanceState.getInt("flag");
            flagLoadMoreData = savedInstanceState.getInt("flagLoadMoreData");
            posters = savedInstanceState.getStringArrayList("posters");
            date = savedInstanceState.getStringArrayList("date");
            title = savedInstanceState.getStringArrayList("title");
            overView = savedInstanceState.getStringArrayList("overView");
            movieId = savedInstanceState.getIntegerArrayList("movieId");
            page = savedInstanceState.getInt("page");

            double[] firstValueArray = savedInstanceState.getDoubleArray("rating");
            Log.d(TAG, "onCreate: savedInstanceState != null");
            for(double d : firstValueArray) rating.add(d);

            movieAdapter = new MovieAdapter(getContext(), posters, title, rating); 
        } else {
            posters = new ArrayList<>();
            movieId = new ArrayList<>();
            title = new ArrayList<>();
            overView = new ArrayList<>();
            date = new ArrayList<>();
            rating = new ArrayList<>();
            Log.d(TAG, "onCreate: savedInstanceState == null");
        }
    }
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
//        savedInstanceState = this.savedInstanceState;
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        setHasOptionsMenu(true);
        moviesGrid = (GridView) view.findViewById(R.id.moviesGrid);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView = (TextView) view.findViewById(R.id.textView_popOrTop);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        if (savedInstanceState!=null) {
            Log.d(TAG, "onCreateView: savedInstanceState != NULL");
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
        } else if (savedInstanceState == null){
            posters = new ArrayList<>();
            movieId = new ArrayList<>();
            title = new ArrayList<>();
            overView = new ArrayList<>();
            date = new ArrayList<>();
            rating = new ArrayList<>();
            Log.d(TAG, "onCreateView: savedInstanceState == NULL");
            page=0;
            moviesGrid.setOnScrollListener(new EndlessScrollListener(6, page) {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    // Triggered only when new data needs to be appended to the list
                    // Add whatever code is needed to append new items to your AdapterView
                    loadMoreData(page);
                    return true; // ONLY if more data is actually being loaded; false otherwise.
                }
            });
            swipeContainer.setRefreshing(true);
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
                    movieAdapter = new MovieAdapter(getActivity(), posters, title, rating);
                    moviesGrid.setAdapter(movieAdapter);
                }
                swipeContainer.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
//                Log.d("MainActivity", "onSuccess: Posters: "+ Arrays.toString(posters));
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Snackbar.make(view, "No Internet!!", Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activty, new NoInternetFragment())
                        .commit();
                progressBar.setVisibility(View.GONE);
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
        if (flagLoadMoreData == 0)
            url = "http://api.themoviedb.org/3/movie/popular?page=" + page + "&" + APIKEY;
        else if (flagLoadMoreData == 1)
            url = "http://api.themoviedb.org/3/movie/top_rated?page=" + page + "&" + APIKEY;

        Log.d(TAG, "loadMoreData: "+url);
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
                Snackbar.make(view, "No Internet!!", Snackbar.LENGTH_LONG).show();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activty, new NoInternetFragment())
                        .commit();
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.d(TAG, "onItemClick: "+position);
//        Intent intent = new Intent(getContext(), MovieDetails.class);
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title.get(position));
        args.putString("date", date.get(position));
        args.putString("poster", "https://image.tmdb.org/t/p/w185" + posters.get(position));
        args.putDouble("rating", rating.get(position));
        args.putString("overView", overView.get(position));
        args.putInt("movieId", movieId.get(position));
        args.putBoolean("activityFlag", false);
        movieDetailsFragment.setArguments(args);
        if (getArguments() != null) {
            if (getArguments().getBoolean("masterDetail")) { // if tablet load the detail into the rightFrame
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rightFrame, movieDetailsFragment)
                        .addToBackStack("moviesDetails")
                        .commit();
            } else { // if not tablet load it into the activity
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activty, movieDetailsFragment)
                        .addToBackStack("moviesDetails")
                        .commit();
            }
        }
        else { // if not tablet too
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activty, movieDetailsFragment)
                    .addToBackStack("moviesDetails")
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //filter 'Navigate between the popular and Top Rated LISTS'
        if (item.getItemId() == R.id.menuFilter){
            if(flag == 1) {
                flag=0;
                flagLoadMoreData=0;
                clearResources();
                textView.setText("Popular");
                url = "http://api.themoviedb.org/3/movie/popular?page=1&"+APIKEY;
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
            else if(flag == 0) {
                flag = 1;
                flagLoadMoreData=1;
                textView.setText("Top rated");
                clearResources();
                url = "http://api.themoviedb.org/3/movie/top_rated?page=1&"+APIKEY;
                filterList(url);
                return super.onOptionsItemSelected(item);
            }
        }
        else if (item.getItemId() == R.id.favorites){
            startActivity(new Intent(getContext(), Favorites.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearResources() {
        posters.clear();
        movieId.clear();
        title.clear();
        overView.clear();
        date.clear();
        rating.clear();
        Log.d(TAG, "optionsItemClicked: "+url);
        page = 1;
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menuFilter);
        menuItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        menuItem = menu.findItem(R.id.favorites);
        menuItem.getIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
