package com.scheffer.erik.popularmovies.moviedatabaseapi.models

import android.os.Parcelable
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.scheffer.erik.popularmovies.database.AppDatabase
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Table(database = AppDatabase::class, allFields = true)
data class Movie(@PrimaryKey(autoincrement = true) var databaseId: Long = 0,
                 var id: Long = 0,
                 var title: String = "",
                 var voteAverage: Float = 0F,
                 var overview: String = "",
                 var posterPath: String = "",
                 var releaseDate: Date = Date()) : Parcelable

data class MovieResultList(val results: List<Movie>)