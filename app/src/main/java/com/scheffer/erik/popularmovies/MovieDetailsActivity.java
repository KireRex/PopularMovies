package com.scheffer.erik.popularmovies;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.Database.FavoriteMovieContracts;
import com.scheffer.erik.popularmovies.Database.FavoriteMovieDbHelper;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.Adapters.MovieReviewAdapter;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.Adapters.MovieTrailerAdapter;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Movie;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Review;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Trailer;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.Tasks.MovieReviewsTask;
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
    private MovieReviewAdapter reviewAdapter;
    private Movie movie;
    private SQLiteDatabase database;
    private long favoriteMovieDatabaseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movie = getIntent().getParcelableExtra(MOVIE_EXTRA_NAME);

        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);
        database = dbHelper.getReadableDatabase();
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

    private void setUpTrailersRecyclerView(Movie movie) {
        RecyclerView trailersRecyclerView = getRecyclerView(R.id.trailers_list);
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

    private void setUpReviewsRecyclerView(Movie movie) {
        RecyclerView reviewsRecyclerView = getRecyclerView(R.id.reviews_list);
        reviewsRecyclerView.setNestedScrollingEnabled(false);
        reviewAdapter = new MovieReviewAdapter(new ArrayList<Review>());
        reviewsRecyclerView.setAdapter(reviewAdapter);
        if (ConnectionUtils.isConnected(this)) {
            new MovieReviewsTask(movie.getId(), new AsyncTaskCompleteListener<List<Review>>() {
                @Override
                public void onTaskComplete(List<Review> results) {
                    reviewAdapter.setReviews(results);
                    reviewAdapter.notifyDataSetChanged();
                }
            }).execute();
        }
    }

    private RecyclerView getRecyclerView(int recyclerViewId) {
        RecyclerView reviewsRecyclerView = findViewById(recyclerViewId);
        reviewsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(layoutManager);
        reviewsRecyclerView.addItemDecoration(
                new DividerItemDecoration(reviewsRecyclerView.getContext(),
                                          layoutManager.getOrientation()));
        return reviewsRecyclerView;
    }

    private void saveFavoriteMovie() {
        favoriteMovieDatabaseId = database.insert(FavoriteMovieContracts.MovieEntry.TABLE_NAME,
                                                  null,
                                                  movie.asContentValues());
    }

    private void deleteFavoriteMovie() {
        int deleteds = database.delete(FavoriteMovieContracts.MovieEntry.TABLE_NAME,
                                       FavoriteMovieContracts.MovieEntry._ID + "=?",
                                       new String[]{String.valueOf(favoriteMovieDatabaseId)});
        if (deleteds > 0) {
            favoriteMovieDatabaseId = -1;
        }
    }

    private void getFavoriteMovieId() {
        Cursor cursor = database.query(FavoriteMovieContracts.MovieEntry.TABLE_NAME,
                                       null,
                                       FavoriteMovieContracts.MovieEntry.COLUMN_EXTERNAL_ID + "=?",
                                       new String[]{String.valueOf(movie.getId())},
                                       null,
                                       null,
                                       null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            favoriteMovieDatabaseId = cursor.getLong(cursor.getColumnIndex(FavoriteMovieContracts.MovieEntry._ID));
        }
        cursor.close();
    }
}