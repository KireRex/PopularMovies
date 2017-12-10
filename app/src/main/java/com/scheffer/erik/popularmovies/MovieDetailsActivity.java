package com.scheffer.erik.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Movie;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Trailer;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.MovieTrailerAdapter;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.Tasks.MovieTrailersTask;
import com.scheffer.erik.popularmovies.Utils.AsyncTaskCompleteListener;
import com.scheffer.erik.popularmovies.Utils.ConnectionUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.scheffer.erik.popularmovies.MovieDatabaseApi.ApiConstants.MOVIES_DATABASE_BASE_POSTER_URL;

public class MovieDetailsActivity extends AppCompatActivity {
    public static String MOVIE_EXTRA_NAME = "movie";

    private MovieTrailerAdapter trailerAdapter;

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

        RecyclerView trailersRecyclerView = findViewById(R.id.trailers_list);
        trailersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(layoutManager);
        trailersRecyclerView.addItemDecoration(
                new DividerItemDecoration(trailersRecyclerView.getContext(),
                                          layoutManager.getOrientation()));
        trailerAdapter = new MovieTrailerAdapter(new ArrayList<Trailer>());
        trailersRecyclerView.setAdapter(trailerAdapter);

        if (ConnectionUtils.isConnected(this)) {
            new MovieTrailersTask(movie.getId(), new AsyncTaskCompleteListener<List<Trailer>>() {
                @Override
                public void onTaskComplete(List<Trailer> results) {
                    trailerAdapter.setTrailers(results);
                    trailerAdapter.notifyDataSetChanged();
                }
            }).execute();
        }
    }
}