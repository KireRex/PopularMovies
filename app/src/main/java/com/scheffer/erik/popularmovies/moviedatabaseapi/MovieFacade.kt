package com.scheffer.erik.popularmovies.moviedatabaseapi

import android.content.Context
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.scheffer.erik.popularmovies.database.getAllMovies
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.MovieResultList
import com.scheffer.erik.popularmovies.utils.isConnected
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
                                               MOVIES_DATABASE_API_KEY).build()
                    request = request.newBuilder().url(url).build()
                    chain.proceed(request)
                }).build()
        retrofit = Retrofit.Builder()
                .baseUrl(MOVIES_DATABASE_BASE_URL)
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

    fun getMovies(criteria: SearchCriteria,
                  processMovies: (List<Movie>) -> Unit,
                  context: Context) =
            if (criteria.isOnlineResource()) {
                getFromOnlineApi(criteria, processMovies, context)
            } else {
                processMovies(getAllMovies() ?: ArrayList())
            }


    private fun getFromOnlineApi(criteria: SearchCriteria,
                                 processMovies: (List<Movie>) -> Unit,
                                 context: Context) {
        if (isConnected(context)) {
            when (criteria) {
                SearchCriteria.POPULAR -> movieService.populars()
                SearchCriteria.TOP_RATED -> movieService.topRateds()
                else -> throw RuntimeException("Only popular and top rated for this method")
            }.enqueue(object : Callback<MovieResultList?> {
                override fun onResponse(call: Call<MovieResultList?>?,
                                        response: Response<MovieResultList?>?) {
                    response?.body()?.results?.let { processMovies(it) }
                }

                override fun onFailure(call: Call<MovieResultList?>?, t: Throwable?) {
                    t?.printStackTrace()
                }
            })

        }
    }

    fun getMovieTrailers(movieId: Long) = movieService.trailers(movieId)

    fun getMovieReviews(movieId: Long) = movieService.reviews(movieId)
}