package com.scheffer.erik.popularmovies.moviedatabaseapi.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.scheffer.erik.popularmovies.R
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Review
import kotlinx.android.synthetic.main.item_review.view.*

import ru.noties.markwon.Markwon

class MovieReviewAdapter(private var reviews: List<Review>)
    : RecyclerView.Adapter<MovieReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
                                  .inflate(R.layout.item_review, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (_, author, content) = reviews[position]
        Markwon.setMarkdown(holder.reviewText, content)
        holder.authorName.text = author
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var reviewText: TextView = itemView.review_text
        var authorName: TextView = itemView.review_author_name
    }

    fun setReviews(reviews: List<Review>) {
        this.reviews = reviews
    }
}