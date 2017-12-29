package com.scheffer.erik.popularmovies

import android.content.ContentUris
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.Menu
import android.view.MenuItem
import com.scheffer.erik.popularmovies.database.FavoriteMovieContract
import com.scheffer.erik.popularmovies.moviedatabaseapi.ApiConstants.MOVIES_DATABASE_BASE_POSTER_URL
import com.scheffer.erik.popularmovies.moviedatabaseapi.MovieFacade
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MovieReviewAdapter
import com.scheffer.erik.popularmovies.moviedatabaseapi.adapters.MovieTrailerAdapter
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.*
import com.scheffer.erik.popularmovies.utils.isConnected
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_movie_details.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsActivity : AppCompatActivity() {

    private var trailerAdapter: MovieTrailerAdapter = MovieTrailerAdapter(ArrayList())
    private var reviewAdapter: MovieReviewAdapter = MovieReviewAdapter(ArrayList())
    private lateinit var movie: Movie
    private var favoriteMovieDatabaseId: Long = -1
    private var trailersLayoutManager: LinearLayoutManager? = null
    private var reviewsLayoutManager: LinearLayoutManager? = null
    private var trailersState: Parcelable? = null
    private var reviewsState: Parcelable? = null

    private var trailers: ArrayList<Trailer> = ArrayList()
    private var reviews: ArrayList<Review> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        movie = intent.getParcelableExtra(MOVIE_EXTRA_NAME)

        getFavoriteMovieId()

        Picasso.with(this)
                .load(MOVIES_DATABASE_BASE_POSTER_URL + "w780//" + movie.posterPath)
                .into(movie_poster)
        movie_title.text = movie.title
        movie_synopsis.text = movie.overview
        movie_rating.text = movie.voteAverage.toString()
        movie_release_date.text = DateFormat.getDateFormat(this).format(movie.releaseDate)
    }

    override fun onResume() {
        super.onResume()
        setUpTrailersRecyclerView(movie)
        setUpReviewsRecyclerView(movie)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_menu, menu)
        if (favoriteMovieDatabaseId >= 0) {
            menu.getItem(0).setIcon(R.drawable.ic_star_yellow_48dp)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.favorite_movie -> {
                if (favoriteMovieDatabaseId >= 0) {
                    item.setIcon(R.drawable.ic_star_border_yellow_48dp)
                    deleteFavoriteMovie()
                } else {
                    item.setIcon(R.drawable.ic_star_yellow_48dp)
                    saveFavoriteMovie()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        trailersState = trailersLayoutManager!!.onSaveInstanceState()
        reviewsState = reviewsLayoutManager!!.onSaveInstanceState()

        outState.putParcelable(TRAILERS_STATE_KEY, trailersState)
        outState.putParcelable(REVIEWS_STATE_KEY, reviewsState)
        outState.putParcelableArrayList(TRAILERS_LIST_KEY, trailers)
        outState.putParcelableArrayList(REVIEWS_LIST_KEY, reviews)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        reviewsState = savedInstanceState.getParcelable(REVIEWS_STATE_KEY)
        trailersState = savedInstanceState.getParcelable(TRAILERS_STATE_KEY)
        trailers = savedInstanceState.getParcelableArrayList(TRAILERS_LIST_KEY)
        reviews = savedInstanceState.getParcelableArrayList(REVIEWS_LIST_KEY)
    }

    private fun setUpTrailersRecyclerView(movie: Movie) {
        if (trailersLayoutManager == null) {
            trailersLayoutManager = LinearLayoutManager(this)
        }
        val trailersRecyclerView = getRecyclerView(R.id.trailers_list, trailersLayoutManager!!)
        if (trailers.isEmpty()) {
            trailersRecyclerView.adapter = trailerAdapter
            if (isConnected(this)) {
                MovieFacade().getMovieTrailers(movie.id).enqueue(object : Callback<TrailerResultList?> {
                    override fun onResponse(call: Call<TrailerResultList?>?,
                                            response: Response<TrailerResultList?>?) {
                        response?.body()?.results.let {
                            trailers = ArrayList(it)
                            trailerAdapter.trailers = trailers
                            trailerAdapter.notifyDataSetChanged()
                            trailersState?.let {
                                trailersLayoutManager!!.onRestoreInstanceState(trailersState)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrailerResultList?>?, t: Throwable?) {
                        t?.printStackTrace()
                    }
                })
            }
        } else {
            trailerAdapter.trailers = trailers
            trailersRecyclerView.adapter = trailerAdapter
        }

    }

    private fun setUpReviewsRecyclerView(movie: Movie) {
        if (reviewsLayoutManager == null) {
            reviewsLayoutManager = LinearLayoutManager(this)
        }
        val reviewsRecyclerView = getRecyclerView(R.id.reviews_list, reviewsLayoutManager!!)
        reviewsRecyclerView.isNestedScrollingEnabled = false
        if (reviews.isEmpty()) {
            reviewsRecyclerView.adapter = reviewAdapter
            if (isConnected(this)) {

                MovieFacade().getMovieReviews(movie.id).enqueue(object : Callback<ReviewResultList?> {
                    override fun onResponse(call: Call<ReviewResultList?>?,
                                            response: Response<ReviewResultList?>?) {
                        response?.body()?.results.let {
                            reviews = ArrayList(it)
                            reviewAdapter.setReviews(reviews)
                            reviewAdapter.notifyDataSetChanged()
                            if (reviewsState != null) {
                                reviewsLayoutManager!!.onRestoreInstanceState(reviewsState)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ReviewResultList?>?, t: Throwable?) {
                        t?.printStackTrace()
                    }
                })
            }
        } else {
            reviewAdapter.setReviews(reviews)
            reviewsRecyclerView.adapter = reviewAdapter
        }
    }

    private fun getRecyclerView(recyclerViewId: Int,
                                layoutManager: LinearLayoutManager): RecyclerView {
        val reviewsRecyclerView = findViewById<RecyclerView>(recyclerViewId)
        reviewsRecyclerView.setHasFixedSize(true)
        reviewsRecyclerView.layoutManager = layoutManager
        reviewsRecyclerView.addItemDecoration(
                DividerItemDecoration(reviewsRecyclerView.context,
                                      layoutManager.orientation))
        return reviewsRecyclerView
    }

    private fun saveFavoriteMovie() {
        val uri = contentResolver.insert(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                         movie.asContentValues())
        favoriteMovieDatabaseId = ContentUris.parseId(uri)
    }

    private fun deleteFavoriteMovie() {
        val deleteds = contentResolver
                .delete(FavoriteMovieContract.MovieEntry.CONTENT_URI.buildUpon()
                                .appendPath(
                                        favoriteMovieDatabaseId.toString())
                                .build(), null, null)
        if (deleteds > 0) {
            favoriteMovieDatabaseId = -1
        }
    }

    private fun getFavoriteMovieId() {
        val cursor = contentResolver
                .query(FavoriteMovieContract.MovieEntry.CONTENT_URI.buildUpon()
                               .appendPath(
                                       movie.id.toString())
                               .build(), null, null, null, null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                favoriteMovieDatabaseId = cursor.getLong(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry._ID))
            }
            cursor.close()
        }
    }

    companion object {
        val MOVIE_EXTRA_NAME = "movie"
        private val REVIEWS_STATE_KEY = "reviews-state"
        private val TRAILERS_STATE_KEY = "trailers-state"
        private val TRAILERS_LIST_KEY = "trailers-list"
        private val REVIEWS_LIST_KEY = "reviews-list"
    }
}