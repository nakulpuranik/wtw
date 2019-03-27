package com.assignment.whatstheweather.adapters;

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

import com.assignment.whatstheweather.Models.SoilTypes;
import com.assignment.whatstheweather.R;
import com.assignment.whatstheweather.utils.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Nakul
 */

public class SoilAdapter extends RecyclerView.Adapter<SoilAdapter.MovieViewHolder> {
    private List<SoilTypes> soilList;
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

    public SoilAdapter(Context context, List<SoilTypes> soilList) {
        this.soilList = soilList;
        mPoolOfBitmap = new HashMap<>();
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.soil_type_item_view, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        SoilTypes soilInfo = soilList.get(position);
        holder.mTitle.setText(soilInfo.getSoilType());

//        if(mPoolOfBitmap.containsKey(movie.getId())){
//            mMoviePoster = (Bitmap) mPoolOfBitmap.get(movie.getId());
//            holder.movieThumbnail.setImageBitmap(mMoviePoster);
//        }
//        else{
//            if(Util.isOnline(context)) {
//                new DownloadImageTask(holder.movieThumbnail).execute(movie);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return soilList.size();
    }
}

