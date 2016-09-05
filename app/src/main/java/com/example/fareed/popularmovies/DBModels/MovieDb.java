package com.example.fareed.popularmovies.DBModels;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Mohammed Fareed on 9/1/2016.
 */
public class MovieDb extends RealmObject {
    public String name;
    public String date;
    public String overView;
    public double rating;
    @PrimaryKey
    public int movieId;
    public String poster;

    public MovieDb(){
    }

    public MovieDb(String name, String date, String overView, double rating, int movieId, String poster){
        this.name = name;
        this.date = date;
        this.overView = overView;
        this.rating = rating;
        this.movieId = movieId;
        this.poster = poster;
    }
}
