package com.scheffer.erik.popularmovies.moviedatabaseapi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.R;
import com.scheffer.erik.popularmovies.moviedatabaseapi.dataclasses.Review;

import java.util.List;

import ru.noties.markwon.Markwon;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private List<Review> reviews;

    public MovieReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Review review = reviews.get(position);
        Markwon.setMarkdown(holder.reviewText, review.getContent());
        holder.authorName.setText(review.getAuthor());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewText;
        TextView authorName;

        ViewHolder(final View itemView) {
            super(itemView);
            this.reviewText = itemView.findViewById(R.id.review_text);
            this.authorName = itemView.findViewById(R.id.review_author_name);
        }
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}