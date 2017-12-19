package com.scheffer.erik.popularmovies.moviedatabaseapi.tasks;

import android.os.AsyncTask;

import com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConnection;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Trailer;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieTrailersTask extends AsyncTask<Void, Void, List<Trailer>> {
    private Long movieId;
    private AsyncTaskCompleteListener<List<Trailer>> listener;

    public MovieTrailersTask(Long movieId, AsyncTaskCompleteListener<List<Trailer>> listener) {
        this.movieId = movieId;
        this.listener = listener;
    }

    @Override
    protected List<Trailer> doInBackground(Void... voids) {
        try {
            return ApiConnection.getMovieTrailers(movieId);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Trailer> trailers) {
        listener.onTaskComplete(trailers);
    }
}