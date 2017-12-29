package com.scheffer.erik.popularmovies.moviedatabaseapi.converter

import android.database.Cursor
import com.scheffer.erik.popularmovies.database.FavoriteMovieContract
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import java.util.*

fun fromCursor(cursor: Cursor): Movie {
    return Movie(
            cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID)),
            cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_TITLE)),
            cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
            cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW)),
            cursor.getString(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_POSTER_PATH)),
            Date(cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE)))
    )
}