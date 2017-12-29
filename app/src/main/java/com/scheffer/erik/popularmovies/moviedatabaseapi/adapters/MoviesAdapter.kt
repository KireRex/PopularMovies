package com.scheffer.erik.popularmovies.moviedatabaseapi.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.scheffer.erik.popularmovies.R
import com.scheffer.erik.popularmovies.moviedatabaseapi.MOVIES_DATABASE_BASE_POSTER_URL
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie.view.*

class MoviesAdapter(context: Context, movies: List<Movie>)
    : ArrayAdapter<Movie>(context, 0, movies) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val returnView = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_movie,
                                                                             parent,
                                                                             false)

        getItem(position)?.let {
            Picasso.with(context)
                    .load(MOVIES_DATABASE_BASE_POSTER_URL + "w780//" + it.posterPath)
                    .into(returnView.movie_poster)
        }

        return returnView
    }
}