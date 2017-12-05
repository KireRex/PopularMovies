package com.scheffer.erik.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.scheffer.erik.popularmovies.MovieDatabaseApi.ApiConnection;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.Movie;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.MoviesAdapter;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.SearchCriteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    GridView movieGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        movieGrid = findViewById(R.id.movie_grid);
        movieGrid.setEmptyView(findViewById(R.id.no_movies_text));
        new MoviesDatabaseTask(SearchCriteria.POPULAR).execute();
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
                new MoviesDatabaseTask(SearchCriteria.POPULAR).execute();
                return true;
            case R.id.top_rated:
                new MoviesDatabaseTask(SearchCriteria.TOP_RATED).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class MoviesDatabaseTask extends AsyncTask<Void, Void, List<Movie>> {
        private SearchCriteria criteria;

        MoviesDatabaseTask(SearchCriteria criteria) {
            this.criteria = criteria;
        }

        @Override
        protected List<Movie> doInBackground(Void... voids) {
            try {
                return ApiConnection.getMovies(criteria);
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            MoviesAdapter moviesAdapter = new MoviesAdapter(MovieListActivity.this, movies);
            movieGrid.setAdapter(moviesAdapter);
        }
    }
}
