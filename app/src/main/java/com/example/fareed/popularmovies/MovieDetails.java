package com.example.fareed.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
    String TAG = "MOVIEDETAILS";
    TextView textView_title, textView_rating, textView_overView, textView_date;
    ImageView image_poster;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        actionBar.setTitle(title);
        String date = intent.getStringExtra("date");
        String poster = intent.getStringExtra("poster");
        double rating = intent.getDoubleExtra("rating", 1.5);
//        Log.d(TAG, "onCreate: "+rating);
        String overView = intent.getStringExtra("overView");

        image_poster = (ImageView) findViewById(R.id.image_poster);
        Picasso.with(this).load(poster).into(image_poster);
        textView_title = (TextView) findViewById(R.id.textView_title);
        textView_title.setText(title);
        textView_rating = (TextView) findViewById(R.id.textView_Rating);
        textView_rating.setText(rating+" / 10.0");
        textView_overView = (TextView) findViewById(R.id.textView_overView);
        textView_overView.setText(overView);
        textView_date = (TextView) findViewById(R.id.textView_date);
        textView_date.setText(date);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating((float) rating);
    }
}
