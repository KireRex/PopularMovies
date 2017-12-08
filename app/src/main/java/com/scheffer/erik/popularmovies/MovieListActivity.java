package com.scheffer.erik.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.scheffer.erik.popularmovies.MovieDatabaseApi.Movie;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.MoviesAdapter;
import com.scheffer.erik.popularmovies.MovieDatabaseApi.SearchCriteria;
import com.scheffer.erik.popularmovies.Utils.AsyncTaskCompleteListener;

import java.util.List;

import static com.scheffer.erik.popularmovies.MovieDetailsActivity.MOVIE_EXTRA_NAME;

public class MovieListActivity extends AppCompatActivity {

    private GridView movieGrid;
    private AsyncTaskCompleteListener<List<Movie>> movieTaskListener;

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

        new MoviesDatabaseTask(SearchCriteria.POPULAR, movieTaskListener).execute();
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
                new MoviesDatabaseTask(SearchCriteria.POPULAR, movieTaskListener).execute();
                return true;
            case R.id.top_rated:
                new MoviesDatabaseTask(SearchCriteria.TOP_RATED, movieTaskListener).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
