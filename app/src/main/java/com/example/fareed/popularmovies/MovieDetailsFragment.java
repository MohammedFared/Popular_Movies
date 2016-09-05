package com.example.fareed.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fareed.popularmovies.DBModels.MovieDb;
import com.example.fareed.popularmovies.Models.ReviewsBean;
import com.example.fareed.popularmovies.Models.TrailersBean;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class MovieDetailsFragment extends Fragment implements View.OnClickListener {
    public MovieDetailsFragment() {
        // Required empty public constructor
    }

    String TAG = "MOVIEDETAILSLOG", baseUrl = "http://api.themoviedb.org/3/movie/", APIKEY = "api_key="+BuildConfig.MOVIE_API_KEY;
    TextView textView_title, textView_rating, textView_overView, textView_date;
    ImageView image_poster;
    RatingBar ratingBar;
    ProgressBar progressBar;
    int movieId;
    LinearLayout dynamicTrailersView, dynamicReviewsView;
    TrailersBean trailersBean;
    ReviewsBean mreviewBean;
    String title, date, poster, overView;
    double rating;
    boolean trailersFlag = false, reviewsFlag = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString("title");
//            ((Favorites)getActivity()).setActionBarTitle(title);
            date = getArguments().getString("date");
            poster = getArguments().getString("poster");
            rating = getArguments().getDouble("rating", 1.5);
//        Log.d(TAG, "onCreate: "+rating);
            overView = getArguments().getString("overView");
            movieId = getArguments().getInt("movieId", 1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movie_details, container, false);
        //initialize views
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_details);
        progressBar.setVisibility(View.GONE);
        image_poster = (ImageView) view.findViewById(R.id.image_poster);
        Picasso.with(getContext()).load(poster).into(image_poster);
        textView_title = (TextView) view.findViewById(R.id.textView_title);
        textView_title.setText(title);
        textView_rating = (TextView) view.findViewById(R.id.textView_Rating);
        textView_rating.setText(rating + " / 10.0");
        textView_overView = (TextView) view.findViewById(R.id.textView_overView);
        textView_overView.setText(overView);
        textView_date = (TextView) view.findViewById(R.id.textView_date);
        textView_date.setText(date);
        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setRating((float) rating);

        view.findViewById(R.id.button_favorite).setOnClickListener(this);

        dynamicReviewsView = (LinearLayout) view.findViewById(R.id.linearLayout_reviews);
        dynamicTrailersView = (LinearLayout) view.findViewById(R.id.linearLayout_trailers);

        view.findViewById(R.id.button_reviews).setOnClickListener(this);
        view.findViewById(R.id.button_trailers).setOnClickListener(this);

        return view;
    }

    private void getReviews() {
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getContext(), baseUrl + movieId + "/reviews?" + APIKEY, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                mreviewBean = gson.fromJson(new String(responseBody), ReviewsBean.class);
                final List<ReviewsBean.ResultsBean> results = mreviewBean.getResults();
                if (results.isEmpty()){
                    TextView textView = new TextView(getContext());
                    textView.setText("No reviews yet!");
                    dynamicReviewsView.addView(textView);
                }
                for (int i = 0; i < results.size(); i++) {
                    TextView textViewAuthor = new TextView(getContext());
                    textViewAuthor.setText(results.get(i).getAuthor());
                    textViewAuthor.setLayoutParams(lp);
                    textViewAuthor.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
                    dynamicReviewsView.addView(textViewAuthor);
                    TextView textViewContent = new TextView(getContext());
                    textViewContent.setText(results.get(i).getContent());
                    textViewContent.setLayoutParams(lp);
                    textViewContent.setPadding(16, 8, 8,8);
                    textViewContent.setTextAppearance(getContext(), android.R.style.TextAppearance_Material_Body1);
                    dynamicReviewsView.addView(textViewContent);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getTrailers() {
        progressBar.setVisibility(View.VISIBLE);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(getContext(), baseUrl + movieId + "/videos?" + APIKEY, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Gson gson = new Gson();
                trailersBean = gson.fromJson(new String(responseBody), TrailersBean.class);
                final List<TrailersBean.ResultsBean> results = trailersBean.getResults();
                if (results.isEmpty()){
                    TextView textView = new TextView(getContext());
                    textView.setText("No trailers yet!");
                    dynamicReviewsView.addView(textView);
                }
                for (int i = 0; i < results.size(); i++) {
                    Button btn = new Button(getContext());
                    btn.setId(i + 1);
                    btn.setText(results.get(i).getName());
                    Log.d(TAG, "onSuccess: " + results.get(i).getName());
                    btn.setLayoutParams(lp);
                    final int finalI = i;
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getContext(), view.getId() + "", Toast.LENGTH_SHORT).show();
                            results.get(finalI).getKey();
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" +
                                        results.get(finalI).getKey())));
                            } catch (ActivityNotFoundException ex) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" +
                                        results.get(finalI).getKey())));
                            }
                        }
                    });
                    dynamicTrailersView.addView(btn);
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_favorite) {
            addMovieToFav();
        }
        else if (view.getId() == R.id.button_trailers){
            if (!trailersFlag) {
                getTrailers();
                trailersFlag = true;
            }
        }
        else if (view.getId() == R.id.button_reviews){
            if (!reviewsFlag) {
                getReviews();
                reviewsFlag = true;
            }
        }
    }

    private void addMovieToFav() {
        Realm realm = Realm.getDefaultInstance();
        try {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MovieDb movieDb = realm.createObject(MovieDb.class, movieId);
                    movieDb.name= title;
                    movieDb.poster = poster;
                    movieDb.movieId = movieId;
                    movieDb.rating = rating;
                    movieDb.overView = overView;
                    movieDb.date = date;
                }
            });
            Toast.makeText(getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
        }
        catch (RealmPrimaryKeyConstraintException exception){
            Log.d(TAG, "saveMovieToDb: "+exception.toString());
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<MovieDb> result = realm.where(MovieDb.class).equalTo("movieId", movieId).findAll();
                    result.deleteAllFromRealm();
                }
            });
            Toast.makeText(getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();

        }
        realm.close();
    }


}
