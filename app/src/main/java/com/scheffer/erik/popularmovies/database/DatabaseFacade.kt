package com.scheffer.erik.popularmovies.database

import com.raizlabs.android.dbflow.kotlinextensions.*
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie_Table

fun getAllMovies(): MutableList<Movie>? = (select from Movie::class).list

fun getMovieByExternalId(externalId: Long) =
        (select from Movie::class
                where (Movie_Table.id eq externalId)).querySingle()