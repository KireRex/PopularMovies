package com.scheffer.erik.popularmovies.utils;

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T result);
}