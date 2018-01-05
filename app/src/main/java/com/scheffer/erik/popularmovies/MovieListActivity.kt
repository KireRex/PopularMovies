package com.scheffer.erik.popularmovies

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.raizlabs.android.dbflow.config.FlowManager
import com.scheffer.erik.popularmovies.moviedatabaseapi.MovieFacade
import com.scheffer.erik.popularmovies.moviedatabaseapi.SearchCriteria
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MoviesAdapter
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Movie
import icepick.Icepick
import icepick.State
import kotlinx.android.synthetic.main.activity_movie_list.*
import org.jetbrains.anko.toast

class MovieListActivity : AppCompatActivity() {

    @State @JvmField var movies: ArrayList<Movie> = ArrayList()
    @State @JvmField var criteria = SearchCriteria.POPULAR
    @State @JvmField var gridState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FlowManager.init(applicationContext)
        setContentView(R.layout.activity_movie_list)
        Icepick.restoreInstanceState(this, savedInstanceState)

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
        super.onSaveInstanceState(outState)
        Icepick.saveInstanceState(this, outState)
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
        MovieFacade().getMovies(criteria, { updateAdapter(it) }, this)
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
}