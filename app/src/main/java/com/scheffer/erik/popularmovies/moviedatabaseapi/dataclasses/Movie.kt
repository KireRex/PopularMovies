package com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses

import android.content.ContentValues
import android.os.Parcelable
import com.scheffer.erik.popularmovies.database.FavoriteMovieContract
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Movie(val id: Long,
                 val title: String,
                 val voteAverage: Float,
                 val overview: String,
                 val posterPath: String,
                 val releaseDate: Date) : Parcelable {

    fun asContentValues(): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_EXTERNAL_ID, id)
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_TITLE, title)
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage)
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW, overview)
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath)
        contentValues.put(FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                          releaseDate.time)
        return contentValues
    }
}