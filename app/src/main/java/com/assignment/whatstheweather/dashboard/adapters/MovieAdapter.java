package com.assignment.animation.animationassignment.dashboard.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.assignment.animation.animationassignment.R;
import com.assignment.animation.animationassignment.model.Movie;
import com.assignment.animation.animationassignment.utility.Util;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nakul
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private List<Movie> moviesList;
    private HashMap<Integer,Object> mPoolOfBitmap;
    Bitmap mMoviePoster = null;
    Context context;

    public class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView mTitle;
        ImageView movieThumbnail;
        RelativeLayout mContainer;

        public MovieViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.titleTV);
            movieThumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
        }
    }

    public MovieAdapter(Context context, List<Movie> moviesList) {
        this.moviesList = moviesList;
        mPoolOfBitmap = new HashMap<>();
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.mTitle.setText(movie.getTitle());

        if(mPoolOfBitmap.containsKey(movie.getId())){
            mMoviePoster = (Bitmap) mPoolOfBitmap.get(movie.getId());
            holder.movieThumbnail.setImageBitmap(mMoviePoster);
        }
        else{
            if(Util.isOnline(context)) {
                new DownloadImageTask(holder.movieThumbnail).execute(movie);
            }
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    private class DownloadImageTask extends AsyncTask<Movie, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(Movie... objs) {
            Movie movie = objs[0];
            String urlDisplay = movie.getThumbnailUrl();
            Bitmap mIcon = null;
            mIcon = Util.getBitmapFromUrl(urlDisplay);
            mPoolOfBitmap.put(movie.getId(),mIcon);
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
