package com.scheffer.erik.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.scheffer.erik.popularmovies.database.FavoriteMovieContract;
import com.scheffer.erik.popularmovies.database.FavoriteMovieDbHelper;
import com.scheffer.erik.popularmovies.moviedatabaseapi.Adapters.MoviesAdapter;
import com.scheffer.erik.popularmovies.moviedatabaseapi.DataClasses.Movie;
import com.scheffer.erik.popularmovies.moviedatabaseapi.SearchCriteria;
import com.scheffer.erik.popularmovies.moviedatabaseapi.Tasks.MoviesDatabaseTask;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;
import com.scheffer.erik.popularmovies.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.scheffer.erik.popularmovies.MovieDetailsActivity.MOVIE_EXTRA_NAME;

public class MovieListActivity extends AppCompatActivity {

    private GridView movieGrid;
    private AsyncTaskCompleteListener<List<Movie>> movieTaskListener;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        FavoriteMovieDbHelper dbHelper = new FavoriteMovieDbHelper(this);
        database = dbHelper.getReadableDatabase();

        movieGrid = findViewById(R.id.movie_grid);
        movieGrid.setEmptyView(findViewById(R.id.no_movies_text));
        movieGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                Intent movieDetailsIntent = new Intent(MovieListActivity.this,
                                                       MovieDetailsActivity.class);
                movieDetailsIntent.putExtra(MOVIE_EXTRA_NAME, movie);
                startActivity(movieDetailsIntent);
            }
        });

        movieTaskListener = new AsyncTaskCompleteListener<List<Movie>>() {
            @Override
            public void onTaskComplete(List<Movie> movies) {
                if (movies.isEmpty()) {
                    Toast.makeText(MovieListActivity.this,
                                   getResources().getString(R.string.retrieve_data_error),
                                   Toast.LENGTH_LONG)
                         .show();
                }
                MoviesAdapter moviesAdapter = new MoviesAdapter(MovieListActivity.this, movies);
                movieGrid.setAdapter(moviesAdapter);
            }
        };

        exectueMoviesTask(SearchCriteria.POPULAR);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                exectueMoviesTask(SearchCriteria.POPULAR);
                return true;
            case R.id.top_rated:
                exectueMoviesTask(SearchCriteria.TOP_RATED);
                return true;
            case R.id.favorites:
                getFavoriteMovies();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exectueMoviesTask(SearchCriteria criteria) {
        if (ConnectionUtils.isConnected(this)) {
            new MoviesDatabaseTask(criteria, movieTaskListener).execute();
        } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void getFavoriteMovies() {
        Cursor cursor = getContentResolver().query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                                   null,
                                                   null,
                                                   null,
                                                   null);
        List<Movie> favorites = new ArrayList<>();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                favorites.add(new Movie(cursor));
            }
            cursor.close();
        }

        movieTaskListener.onTaskComplete(favorites);
    }
}
