package com.scheffer.erik.popularmovies.moviedatabaseapi.Tasks;

import android.os.AsyncTask;

import com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConnection;
import com.scheffer.erik.popularmovies.moviedatabaseapi.DataClasses.Review;
import com.scheffer.erik.popularmovies.utils.AsyncTaskCompleteListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MovieReviewsTask extends AsyncTask<Void, Void, List<Review>> {
    private Long movieId;
    private AsyncTaskCompleteListener<List<Review>> listener;

    public MovieReviewsTask(Long movieId, AsyncTaskCompleteListener<List<Review>> listener) {
        this.movieId = movieId;
        this.listener = listener;
    }

    @Override
    protected List<Review> doInBackground(Void... voids) {
        try {
            return ApiConnection.getMovieReviews(movieId);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Review> reviews) {
        listener.onTaskComplete(reviews);
    }
}