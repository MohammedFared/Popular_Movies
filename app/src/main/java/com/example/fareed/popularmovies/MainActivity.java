package com.example.fareed.popularmovies;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.GridView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    public String[] posters = new String[20];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;
        String url = "http://api.themoviedb.org/3/movie/popular?page=1&api_key=3a624f843f50800c01512368404f203e";
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(this, url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                MovieBean movieBean = gson.fromJson(new String(responseBody), MovieBean.class);
                List < MovieBean.ResultsBean > results = movieBean.getResults();
                int i = 0;
                for (MovieBean.ResultsBean result : results){
                    posters[i] = "image.tmdb.org/t/p/w185"+result.getPoster_path();
                    i++;
                }
                Log.d("MainActivity", "onSuccess: Posters: "+ Arrays.toString(posters));
                GridView gridView = (GridView) findViewById(R.id.moviesGrid);
                gridView.setAdapter(new MovieAdapter(context, posters));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }
}
