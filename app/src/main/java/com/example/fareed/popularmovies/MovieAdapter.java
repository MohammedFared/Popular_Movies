package com.example.fareed.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import it.sephiroth.android.library.picasso.Picasso;

/**
 * Created by Mohammed Fareed on 8/1/2016.
 */
public class MovieAdapter extends BaseAdapter {

    private Context context;
    String[] posterUrls;

    LayoutInflater inflater;
    public MovieAdapter(Context context, String[] posterUrl){
        this.context = context;
        this.posterUrls=posterUrl;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return posterUrls.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            Log.d("MovieAdapter", "getView: not recycled");

            convertView = inflater.inflate(R.layout.single_item, parent, false);//set layout for displaying items
        }

        Log.d("MovieAdapter", "getView: "+posterUrls[position]);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.moviePoster);
        Picasso.with(context).load(posterUrls[position]).fit().into(imageView);
        return convertView;
    }
}
