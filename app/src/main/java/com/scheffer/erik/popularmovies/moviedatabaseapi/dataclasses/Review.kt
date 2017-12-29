package com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Review(val id: String,
                  val author: String,
                  val content: String,
                  val url: String) : Parcelable

data class ReviewResultList(val results: List<Review>)