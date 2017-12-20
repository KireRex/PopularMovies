package com.scheffer.erik.popularmovies;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.database.FavoriteMovieContract;
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MovieReviewAdapter;
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MovieTrailerAdapter;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Movie;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Review;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Trailer;
import com.scheffer.erik.popularmovies.moviedatabaseapi.tasks.MovieReviewsTask;
import com.scheffer.erik.popularmovies.moviedatabaseapi.tasks.MovieTrailersTask;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConstants.MOVIES_DATABASE_BASE_POSTER_URL;
import static com.scheffer.erik.popularmovies.utils.ConnectionUtilsKt.isConnected;

public class MovieDetailsActivity extends AppCompatActivity {
    public static String MOVIE_EXTRA_NAME = "movie";
    private static String REVIEWS_STATE_KEY = "reviews-state";
    private static String TRAILERS_STATE_KEY = "trailers-state";
    private static String TRAILERS_LIST_KEY = "trailers-list";
    private static String REVIEWS_LIST_KEY = "reviews-list";

    private MovieTrailerAdapter trailerAdapter;
    private MovieReviewAdapter reviewAdapter;
    private Movie movie;
    private long favoriteMovieDatabaseId = -1;
    private LinearLayoutManager trailersLayoutManager;
    private LinearLayoutManager reviewsLayoutManager;
    private Parcelable trailersState;
    private Parcelable reviewsState;

    private ArrayList<Trailer> trailers;
    private ArrayList<Review> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movie = getIntent().getParcelableExtra(MOVIE_EXTRA_NAME);

        getFavoriteMovieId();

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

    @Override
    protected void onResume() {
        super.onResume();
        setUpTrailersRecyclerView(movie);
        setUpReviewsRecyclerView(movie);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        if (favoriteMovieDatabaseId >= 0) {
            menu.getItem(0).setIcon(R.drawable.ic_star_yellow_48dp);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.favorite_movie:
                if (favoriteMovieDatabaseId >= 0) {
                    item.setIcon(R.drawable.ic_star_border_yellow_48dp);
                    deleteFavoriteMovie();
                } else {
                    item.setIcon(R.drawable.ic_star_yellow_48dp);
                    saveFavoriteMovie();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        trailersState = trailersLayoutManager.onSaveInstanceState();
        reviewsState = reviewsLayoutManager.onSaveInstanceState();

        outState.putParcelable(TRAILERS_STATE_KEY, trailersState);
        outState.putParcelable(REVIEWS_STATE_KEY, reviewsState);
        outState.putParcelableArrayList(TRAILERS_LIST_KEY, trailers);
        outState.putParcelableArrayList(REVIEWS_LIST_KEY, reviews);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        reviewsState = savedInstanceState.getParcelable(REVIEWS_STATE_KEY);
        trailersState = savedInstanceState.getParcelable(TRAILERS_STATE_KEY);
        trailers = savedInstanceState.getParcelableArrayList(TRAILERS_LIST_KEY);
        reviews = savedInstanceState.getParcelableArrayList(REVIEWS_LIST_KEY);
    }

    private void setUpTrailersRecyclerView(Movie movie) {
        if (trailersLayoutManager == null) {
            trailersLayoutManager = new LinearLayoutManager(this);
        }
        RecyclerView trailersRecyclerView = getRecyclerView(R.id.trailers_list,
                                                            trailersLayoutManager);
        trailerAdapter = new MovieTrailerAdapter(new ArrayList<Trailer>());
        if (trailers == null) {
            trailersRecyclerView.setAdapter(trailerAdapter);
            if (isConnected(this)) {
                new MovieTrailersTask(movie.getId(),
                                      new AsyncTaskCompleteListener<List<Trailer>>() {
                                          @Override
                                          public void onTaskComplete(List<Trailer> results) {
                                              trailers = new ArrayList<>(results);
                                              trailerAdapter.setTrailers(trailers);
                                              trailerAdapter.notifyDataSetChanged();
                                              if (trailersState != null) {
                                                  trailersLayoutManager.onRestoreInstanceState(
                                                          trailersState);
                                              }
                                          }
                                      }).execute();
            }
        } else {
            trailerAdapter.setTrailers(trailers);
            trailersRecyclerView.setAdapter(trailerAdapter);
        }

    }

    private void setUpReviewsRecyclerView(Movie movie) {
        if (reviewsLayoutManager == null) {
            reviewsLayoutManager = new LinearLayoutManager(this);
        }
        RecyclerView reviewsRecyclerView = getRecyclerView(R.id.reviews_list, reviewsLayoutManager);
        reviewsRecyclerView.setNestedScrollingEnabled(false);
        reviewAdapter = new MovieReviewAdapter(new ArrayList<Review>());
        if (reviews == null) {
            reviewsRecyclerView.setAdapter(reviewAdapter);
            if (isConnected(this)) {
                new MovieReviewsTask(movie.getId(), new AsyncTaskCompleteListener<List<Review>>() {
                    @Override
                    public void onTaskComplete(List<Review> results) {
                        reviews = new ArrayList<>(results);
                        reviewAdapter.setReviews(reviews);
                        reviewAdapter.notifyDataSetChanged();
                        if (reviewsState != null) {
                            reviewsLayoutManager.onRestoreInstanceState(reviewsState);
                        }
                    }
                }).execute();
            }
        } else {
            reviewAdapter.setReviews(reviews);
            reviewsRecyclerView.setAdapter(reviewAdapter);
        }
    }

    private RecyclerView getRecyclerView(int recyclerViewId, LinearLayoutManager layoutManager) {
        RecyclerView reviewsRecyclerView = findViewById(recyclerViewId);
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setLayoutManager(layoutManager);
        reviewsRecyclerView.addItemDecoration(
                new DividerItemDecoration(reviewsRecyclerView.getContext(),
                                          layoutManager.getOrientation()));
        return reviewsRecyclerView;
    }

    private void saveFavoriteMovie() {
        Uri uri = getContentResolver().insert(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                              movie.asContentValues());
        favoriteMovieDatabaseId = ContentUris.parseId(uri);
    }

    private void deleteFavoriteMovie() {
        int deleteds = getContentResolver()
                .delete(FavoriteMovieContract.MovieEntry.CONTENT_URI.buildUpon()
                                                                    .appendPath(
                                                                            String.valueOf(
                                                                                    favoriteMovieDatabaseId))
                                                                    .build(),
                        null,
                        null);
        if (deleteds > 0) {
            favoriteMovieDatabaseId = -1;
        }
    }

    private void getFavoriteMovieId() {
        Cursor cursor = getContentResolver()
                .query(FavoriteMovieContract.MovieEntry.CONTENT_URI.buildUpon()
                                                                   .appendPath(
                                                                           String.valueOf(
                                                                                   movie.getId()))
                                                                   .build(),
                       null,
                       null,
                       null,
                       null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                favoriteMovieDatabaseId = cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry._ID));
            }
            cursor.close();
        }
    }
}