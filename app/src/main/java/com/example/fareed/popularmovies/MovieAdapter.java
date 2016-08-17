package com.example.fareed.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Mohammed Fareed on 8/1/2016.
 */
public class MovieAdapter extends BaseAdapter {

    private Context context;
    ArrayList<String> posterUrls, title;
    ArrayList<Double> rating;

    LayoutInflater inflater;
    public MovieAdapter(Context context, ArrayList<String> posterUrl, ArrayList<String> title, ArrayList<Double> rating){
        this.context = context;
        this.posterUrls=posterUrl;
        this.title = title;
        this.rating = rating;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return posterUrls.size();
    }

    @Override
    public Object getItem(int i) {
        return posterUrls.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            convertView = inflater.inflate(R.layout.single_item, null);//set layout for displaying items
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_itemPoster);
        Picasso.with(context).load(posterUrls.get(position)).into(imageView);

        TextView textView = (TextView) convertView.findViewById(R.id.textView_itemTitle);
        textView.setText(title.get(position));

        RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar_itemRating);
        double ratingDouble = rating.get(position);
        ratingBar.setRating((float) ratingDouble);

        return convertView;
    }
}
