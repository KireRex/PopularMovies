package com.scheffer.erik.popularmovies.MovieDatabaseApi;

public class Movie {
    private String title;
    private float vote_average;
    private String overview;
    private String posterPath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVote_average() {
        return vote_average;
    }

    public void setVote_average(float vote_average) {
        this.vote_average = vote_average;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", vote_average=" + vote_average +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                '}';
    }
}
