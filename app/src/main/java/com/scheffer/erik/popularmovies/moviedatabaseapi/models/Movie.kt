package com.scheffer.erik.popularmovies.moviedatabaseapi.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Movie(val id: Long,
                 val title: String,
                 val voteAverage: Float,
                 val overview: String,
                 val posterPath: String,
                 val releaseDate: Date) : Parcelable

data class MovieResultList(val results: List<Movie>)