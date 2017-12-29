package com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trailer(val id: String, val key: String, val name: String, val site: String) : Parcelable

data class TrailerResultList(val results: List<Trailer>)