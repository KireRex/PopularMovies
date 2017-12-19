package com.scheffer.erik.popularmovies.moviedatabaseapi.tasks;

import android.os.AsyncTask;

import com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConnection;
import com.scheffer.erik.popularmovies.moviedatabaseapi.SearchCriteria;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Movie;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MoviesDatabaseTask extends AsyncTask<Void, Void, List<Movie>> {
    private SearchCriteria criteria;

    private AsyncTaskCompleteListener<List<Movie>> listener;

    public MoviesDatabaseTask(SearchCriteria criteria,
                              AsyncTaskCompleteListener<List<Movie>> listener) {
        this.criteria = criteria;
        this.listener = listener;
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
        listener.onTaskComplete(movies);
    }
}