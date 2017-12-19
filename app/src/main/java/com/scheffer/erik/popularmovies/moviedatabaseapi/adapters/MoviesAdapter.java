package com.scheffer.erik.popularmovies.moviedatabaseapi.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.scheffer.erik.popularmovies.R;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConstants.MOVIES_DATABASE_BASE_POSTER_URL;

public class MoviesAdapter extends ArrayAdapter<Movie> {
    public MoviesAdapter(@NonNull Context context, List<Movie> movies) {
        super(context, 0, movies);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_movie, parent, false);
        }

        Movie movie = getItem(position);
        if (movie != null) {
            Picasso.with(getContext())
                    .load(MOVIES_DATABASE_BASE_POSTER_URL + "w780//" + movie.getPosterPath())
                    .into((ImageView) convertView.findViewById(R.id.movie_poster));
        }

        return convertView;
    }
}