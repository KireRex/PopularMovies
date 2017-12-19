package com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.scheffer.erik.popularmovies.database.FavoriteMovieContract;

import java.util.Date;

public class Movie implements Parcelable {
    private long id;
    private String title;
    private float voteAverage;
    private String overview;
    private String posterPath;
    private Date releaseDate;

    public Movie(long id,
                 String title,
                 float voteAverage,
                 String overview,
                 String posterPath,
                 Date releaseDate) {
        this.id = id;
        this.title = title;
        this.voteAverage = voteAverage;
        this.overview = overview;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
    }

    private Movie(Parcel in) {
        id = in.readLong();
        title = in.readString();
        voteAverage = in.readFloat();
        overview = in.readString();
        posterPath = in.readString();
        releaseDate = new Date(in.readLong());
    }

    public ContentValues asContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID, id);
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_TITLE, title);
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                          releaseDate.getTime());
        return contentValues;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeFloat(voteAverage);
        dest.writeString(overview);
        dest.writeString(posterPath);
        dest.writeLong(releaseDate.getTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(float voteAverage) {
        this.voteAverage = voteAverage;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", voteAverage=" + voteAverage +
                ", overview='" + overview + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", releaseDate=" + releaseDate +
                '}';
    }
}
