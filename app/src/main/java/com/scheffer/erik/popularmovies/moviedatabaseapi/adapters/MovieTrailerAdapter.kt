package com.scheffer.erik.popularmovies.moviedatabaseapi.adapters

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.scheffer.erik.popularmovies.R
import com.scheffer.erik.popularmovies.moviedatabaseapi.models.Trailer
import kotlinx.android.synthetic.main.item_trailer.view.*
import org.jetbrains.anko.toast

class MovieTrailerAdapter(var trailers: List<Trailer>)
    : RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context)
                               .inflate(R.layout.item_trailer, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val trailer = trailers[position]
        holder.trailerText.text = trailer.name
        holder.trailer = trailer
    }

    override fun getItemCount() = trailers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var trailerText: TextView = itemView.trailer_text
        lateinit var trailer: Trailer

        init {
            itemView.setOnClickListener {
                val youtubeActivity = Intent(Intent.ACTION_VIEW, buildYoutubeUri(trailer.key))
                if (youtubeActivity.resolveActivity(itemView.context
                                                            .packageManager) != null) {
                    itemView.context.startActivity(youtubeActivity)
                } else {
                    itemView.context.toast(R.string.nothing_to_show_video)
                }
            }
        }

        private fun buildYoutubeUri(key: String) =
                Uri.parse("https://www.youtube.com/watch")
                        .buildUpon()
                        .appendQueryParameter("v", key)
                        .build()
    }
}