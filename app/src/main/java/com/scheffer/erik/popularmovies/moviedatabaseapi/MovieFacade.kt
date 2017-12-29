package com.scheffer.erik.popularmovies.moviedatabaseapi

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MovieFacade {
    private val retrofit: Retrofit
    private val movieService: MovieService

    init {
        val client = OkHttpClient.Builder().addInterceptor(
                { chain ->
                    var request = chain.request()
                    val url = request.url()
                            .newBuilder()
                            .addQueryParameter("api_key",
                                               ApiConstants.MOVIES_DATABASE_API_KEY).build()
                    request = request.newBuilder().url(url).build()
                    chain.proceed(request)
                }).build()
        retrofit = Retrofit.Builder()
                .baseUrl(ApiConstants.MOVIES_DATABASE_BASE_URL)
                .addConverterFactory(
                        GsonConverterFactory.create(
                                GsonBuilder()
                                        .setDateFormat("yyyy-MM-dd")
                                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                        .create()))
                .client(client)
                .build()
        movieService = retrofit.create(MovieService::class.java)
    }

    fun getMovies(criteria: SearchCriteria) =
            when (criteria) {
                SearchCriteria.POPULAR -> movieService.populars()
                SearchCriteria.TOP_RATED -> movieService.topRateds()
                else -> throw RuntimeException("Only popular and top rated for this method")
            }

    fun getMovieTrailers(movieId: Long) = movieService.trailers(movieId)

    fun getMovieReviews(movieId: Long)= movieService.reviews(movieId)
}