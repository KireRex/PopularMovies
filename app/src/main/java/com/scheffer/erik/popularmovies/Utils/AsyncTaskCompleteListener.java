package com.scheffer.erik.popularmovies.Utils;

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
}