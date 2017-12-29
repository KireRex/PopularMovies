package com.scheffer.erik.popularmovies.moviedatabaseapi

import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.MovieResultList
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.ReviewResultList
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.TrailerResultList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {
    @GET("popular")
    fun populars(): Call<MovieResultList>

    @GET("top_rated")
    fun topRateds(): Call<MovieResultList>

    @GET("{id}/videos")
    fun trailers(@Path("id") movieId: Long): Call<TrailerResultList>

    @GET("{id}/reviews")
    fun reviews(@Path("id") movieId: Long): Call<ReviewResultList>
}