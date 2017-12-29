package com.scheffer.erik.popularmovies.moviedatabaseapi.converter

import android.content.ContentValues
import android.database.Cursor
import com.scheffer.erik.popularmovies.database.FavoriteMovieContract
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import java.util.*

fun fromCursor(cursor: Cursor) =
        Movie(cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID)),
              cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_TITLE)),
              cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
              cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW)),
              cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_POSTER_PATH)),
              Date(cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE))))

fun Movie.toContentValues(): ContentValues {
    val contentValues = ContentValues()
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID, this.id)
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_TITLE, this.title)
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, this.voteAverage)
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW, this.overview)
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_POSTER_PATH, this.posterPath)
    contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                      this.releaseDate.time)
    return contentValues
}