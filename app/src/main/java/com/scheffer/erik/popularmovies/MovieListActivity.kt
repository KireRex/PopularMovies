package com.scheffer.erik.popularmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.scheffer.erik.popularmovies.MovieDetailsActivity.Companion.MOVIE_EXTRA_NAME
import com.scheffer.erik.popularmovies.database.FavoriteMovieContract
import com.scheffer.erik.popularmovies.moviedatabaseapi.MovieFacade
import com.scheffer.erik.popularmovies.moviedatabaseapi.SearchCriteria
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MoviesAdapter
import com.scheffer.erik.popularmovies.moviedatabaseapi.converter.fromCursor
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Movie
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.MovieResultList
import com.scheffer.erik.popularmovies.utils.isConnected
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieListActivity : AppCompatActivity() {

    private var movies: ArrayList<Movie> = ArrayList()
    private var criteria = SearchCriteria.POPULAR
    private var gridState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        movie_grid.emptyView = no_movies_text
        movie_grid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            val movieDetailsIntent = Intent(this@MovieListActivity,
                                            MovieDetailsActivity::class.java)
            movieDetailsIntent.putExtra(MOVIE_EXTRA_NAME,
                                        parent.getItemAtPosition(position) as Movie)
            startActivity(movieDetailsIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (movies.isEmpty() || criteria == SearchCriteria.FAVORITE) {
            executeMoviesTask()
        } else {
            updateAdapter(movies)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(MOVIES_LIST_KEY, movies)
        outState.putSerializable(CRITERIA_KEY, criteria)
        outState.putParcelable(GRID_STATE_KEY, movie_grid.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        movies = savedInstanceState.getParcelableArrayList(MOVIES_LIST_KEY)
        criteria = savedInstanceState.getSerializable(CRITERIA_KEY) as SearchCriteria
        gridState = savedInstanceState.getParcelable(GRID_STATE_KEY)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        criteria = when (item.itemId) {
            R.id.most_popular -> SearchCriteria.POPULAR
            R.id.top_rated -> SearchCriteria.TOP_RATED
            R.id.favorites -> SearchCriteria.FAVORITE
            else -> return super.onOptionsItemSelected(item)
        }
        executeMoviesTask()
        return true
    }

    private fun executeMoviesTask() {
        if (criteria == SearchCriteria.FAVORITE) {
            getFavoriteMovies()
        } else {
            if (isConnected(this)) {
                MovieFacade().getMovies(criteria).enqueue(object : Callback<MovieResultList?> {
                    override fun onResponse(call: Call<MovieResultList?>?,
                                            response: Response<MovieResultList?>?) {
                        response?.body()?.results?.let { updateAdapter(it) }
                    }

                    override fun onFailure(call: Call<MovieResultList?>?, t: Throwable?) {
                        t?.printStackTrace()
                    }
                })
            } else {
                toast(R.string.no_connection)
            }
        }
    }

    private fun updateAdapter(result: List<Movie>) {
        if (result.isEmpty()) {
            toast(resources.getString(R.string.retrieve_data_error))
        }
        movies = ArrayList(result)
        val moviesAdapter = MoviesAdapter(this@MovieListActivity, movies)
        movie_grid.adapter = moviesAdapter
        gridState?.let { movie_grid.onRestoreInstanceState(gridState) }
    }

    private fun getFavoriteMovies() {
        val cursor = contentResolver.query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                           null,
                                           null,
                                           null,
                                           null)
        movies = ArrayList()
        cursor?.let {
            it.moveToFirst()
            while (!it.isAfterLast) {
                movies.add(fromCursor(it))
                it.moveToNext()
            }
        }
        cursor?.close()

        updateAdapter(movies)
    }

    companion object {
        private val MOVIES_LIST_KEY = "movies-list"
        private val CRITERIA_KEY = "criteria"
        private val GRID_STATE_KEY = "grid-state"
    }
}