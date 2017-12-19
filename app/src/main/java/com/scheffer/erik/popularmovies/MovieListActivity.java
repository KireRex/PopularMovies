package com.scheffer.erik.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.scheffer.erik.popularmovies.database.FavoriteMovieContract;
import com.scheffer.erik.popularmovies.moviedatabaseapi.SearchCriteria;
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MoviesAdapter;
import com.scheffer.erik.popularmovies.moviedatabaseapi.converter.MovieConverter;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Movie;
import com.scheffer.erik.popularmovies.moviedatabaseapi.tasks.MoviesDatabaseTask;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;
import com.scheffer.erik.popularmovies.utils.ConnectionUtils;

import java.util.ArrayList;
import java.util.List;

import static com.scheffer.erik.popularmovies.MovieDetailsActivity.MOVIE_EXTRA_NAME;

public class MovieListActivity extends AppCompatActivity {
    private static final String MOVIES_LIST_KEY = "movies-list";
    private static final String CRITERIA_KEY = "criteria";

    private GridView movieGrid;
    private AsyncTaskCompleteListener<List<Movie>> movieTaskListener;
    private ArrayList<Movie> movies;
    private SearchCriteria criteria = SearchCriteria.POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

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
            public void onTaskComplete(List<Movie> result) {
                if (result.isEmpty()) {
                    Toast.makeText(MovieListActivity.this,
                                   getResources().getString(R.string.retrieve_data_error),
                                   Toast.LENGTH_LONG)
                         .show();
                }
                movies = new ArrayList<>(result);
                MoviesAdapter moviesAdapter = new MoviesAdapter(MovieListActivity.this, movies);
                movieGrid.setAdapter(moviesAdapter);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (movies == null || movies.isEmpty() || criteria.equals(SearchCriteria.FAVORITE)) {
            exectueMoviesTask(criteria);
        } else {
            movieTaskListener.onTaskComplete(movies);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIES_LIST_KEY, movies);
        outState.putSerializable(CRITERIA_KEY, criteria);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        movies = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY);
        criteria = (SearchCriteria) savedInstanceState.getSerializable(CRITERIA_KEY);
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
                criteria = SearchCriteria.POPULAR;
                exectueMoviesTask(criteria);
                return true;
            case R.id.top_rated:
                criteria = SearchCriteria.TOP_RATED;
                exectueMoviesTask(criteria);
                return true;
            case R.id.favorites:
                criteria = SearchCriteria.FAVORITE;
                exectueMoviesTask(criteria);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exectueMoviesTask(SearchCriteria criteria) {
        if (criteria.equals(SearchCriteria.FAVORITE)) {
            getFavoriteMovies();
        } else {
            if (ConnectionUtils.isConnected(this)) {
                new MoviesDatabaseTask(criteria, movieTaskListener).execute();
            } else {
                Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getFavoriteMovies() {
        Cursor cursor = getContentResolver().query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                                   null,
                                                   null,
                                                   null,
                                                   null);
        ArrayList<Movie> favorites = new ArrayList<>();
        if (cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                favorites.add(MovieConverter.fromCursor(cursor));
            }
            cursor.close();
        }

        movies = favorites;
        movieTaskListener.onTaskComplete(movies);
    }
}
