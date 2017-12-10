package com.scheffer.erik.popularmovies.MovieDatabaseApi;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scheffer.erik.popularmovies.MovieDatabaseApi.DataClasses.Trailer;
import com.scheffer.erik.popularmovies.R;

import java.util.List;

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {

    private List<Trailer> trailers;

    public MovieTrailerAdapter(List<Trailer> trailers) {
        this.trailers = trailers;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                                  .inflate(R.layout.item_trailer, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.trailerText.setText(trailer.getName());
        holder.setTrailer(trailer);
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView trailerText;
        Trailer trailer;

        ViewHolder(final View itemView) {
            super(itemView);
            this.trailerText = itemView.findViewById(R.id.trailer_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (trailer != null) {
                        Intent youtubeActivity =
                                new Intent(Intent.ACTION_VIEW, buildYoutubeUri(trailer.getKey()));
                        itemView.getContext().startActivity(youtubeActivity);
                    }
                }
            });
        }

        void setTrailer(Trailer trailer) {
            this.trailer = trailer;
        }

        private Uri buildYoutubeUri(String key) {
            return Uri.parse("https://www.youtube.com/watch")
                      .buildUpon()
                      .appendQueryParameter("v", key)
                      .build();
        }
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}