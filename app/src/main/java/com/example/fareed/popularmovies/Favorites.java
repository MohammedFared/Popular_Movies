package com.example.fareed.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.fareed.popularmovies.DBModels.MovieDb;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class Favorites extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayList<String> posters = new ArrayList<>(), title = new ArrayList<>(), date = new ArrayList<>(), overView = new ArrayList<>();
    ArrayList<Integer> movieId = new ArrayList<>();
    ArrayList<Double> rating = new ArrayList<>();
    MovieAdapter movieAdapter;
    Realm realm;
    GridView favoritesGrid;
    public void setActionBarTitle(String title){
        getSupportActionBar().setTitle(title);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        getSupportActionBar().setTitle("Favorites");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm= Realm.getDefaultInstance();

        favoritesGrid = (GridView) findViewById(R.id.favoritesGrid);
        favoritesGrid.setOnItemClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RealmResults<MovieDb> favorites = realm.where(MovieDb.class).findAll();
        for (MovieDb result : favorites) {
            posters.add(result.poster);
            movieId.add(result.movieId);
            title.add(result.name);
            overView.add(result.overView);
            date.add(result.date);
            rating.add(result.rating);
            movieAdapter = new MovieAdapter(this, posters, title, rating);
            favoritesGrid.setAdapter(movieAdapter);
        }
    }

    @Override
    protected void onStop() {
        posters.clear();
        movieId.clear();
        title.clear();
        overView.clear();
        date.clear();
        rating.clear();
        super.onStop();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        Intent intent = new Intent(this, MovieDetails.class);
//        intent.putExtra("title", title.get(position));
//        intent.putExtra("date", date.get(position));
//        intent.putExtra("poster", posters.get(position));
//            intent.putExtra("rating", rating.get(position));
//        intent.putExtra("overView", overView.get(position));
//        intent.putExtra("movieId", movieId.get(position));
//        startActivity(intent);
        MovieDetailsFragment movieDetailsFragment = new MovieDetailsFragment();
        Bundle args = new Bundle();
        args.putString("title", title.get(position));
        args.putString("date", date.get(position));
        args.putString("poster", posters.get(position));
        Log.d("favoritesLOG", "onItemClick: "+posters.get(position));
        args.putBoolean("activityFlag", true);
        args.putDouble("rating", rating.get(position));
        args.putString("overView", overView.get(position));
        args.putInt("movieId", movieId.get(position));
        movieDetailsFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.favorites_activity, movieDetailsFragment)
                .addToBackStack("favorites")
                .commit();
    }
}
