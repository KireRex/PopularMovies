package com.scheffer.erik.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.MovieDatabaseApi.Movie;
import com.squareup.picasso.Picasso;

import static com.scheffer.erik.popularmovies.MovieDatabaseApi.ApiConstants.MOVIES_DATABASE_BASE_POSTER_URL;

public class MovieDetailsActivity extends AppCompatActivity {

    public static String MOVIE_EXTRA_NAME = "movie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Movie movie = getIntent().getParcelableExtra(MOVIE_EXTRA_NAME);
        Picasso.with(this)
               .load(MOVIES_DATABASE_BASE_POSTER_URL + "w780//" + movie.getPosterPath())
               .into((ImageView) findViewById(R.id.movie_poster));
        ((TextView) findViewById(R.id.movie_title)).setText(movie.getTitle());
        ((TextView) findViewById(R.id.movie_synopsis)).setText(movie.getOverview());
        ((TextView) findViewById(R.id.movie_rating)).setText(String.valueOf(movie.getVoteAverage()));

        ((TextView) findViewById(R.id.movie_release_date))
                .setText(DateFormat.getDateFormat(this)
                                   .format(movie.getReleaseDate()));
    }
}