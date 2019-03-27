package com.assignment.animation.animationassignment.dashboard;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.assignment.animation.animationassignment.R;
import com.assignment.animation.animationassignment.dashboard.adapters.MovieAdapter;
import com.assignment.animation.animationassignment.dashboard.listeners.ClickListeners;
import com.assignment.animation.animationassignment.dashboard.listeners.RecyclerTouchListener;
import com.assignment.animation.animationassignment.details.DetailsActivity;
import com.assignment.animation.animationassignment.model.Movie;
import com.assignment.animation.animationassignment.utility.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DashboardActivity extends AppCompatActivity implements ClickListeners {

    private RecyclerView recyclerView;
    private ArrayList<Movie> movieList;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        recyclerView = findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(DashboardActivity.this,movieList);

        int colSpan = Util.calculateNoOfColumns(DashboardActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, colSpan);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(colSpan, Util.dpToPx(DashboardActivity.this,10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,recyclerView,this));

        prepareMovies();
    }

    private void prepareMovies() {
        /*for(int index=1;index<30;index++){
            movieList.add(new Movie(index,"Matrix","https://upload.wikimedia.org/wikipedia/en/9/9a/The_Matrix_soundtrack_cover.jpg","https://upload.wikimedia.org/wikipedia/en/9/9a/The_Matrix_soundtrack_cover.jpg"));
        }*/
        try{
            InputStream inputStream = DashboardActivity.this.getResources().openRawResource(R.raw.movies);
            String movieJsonStr = new Scanner(inputStream).useDelimiter("\\A").next();
            JSONArray movieJsonArr = new JSONArray(movieJsonStr);
            for(int index=0;index<movieJsonArr.length();index++){
                JSONObject movieDetails = (JSONObject) movieJsonArr.get(index);
                Movie movie = new Movie(
                                        movieDetails.getInt("id"),
                                        movieDetails.getString("title"),
                                        movieDetails.getString("imageUrl"),
                                        movieDetails.getString("thumbnailUrl")
                                        );
                movieList.add(movie);
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                 findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(DashboardActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.EXTRA_PARAM_ID, movieList.get(position).getImageUrl());
        intent.putExtra(DetailsActivity.EXTRA_PARAM_TITLE, movieList.get(position).getTitle());

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.
                makeSceneTransitionAnimation(DashboardActivity.this,
                        new Pair<View, String>((view.findViewById(R.id.thumbnail)), DetailsActivity.VIEW_NAME_HEADER_IMAGE),
                        new Pair<View, String>(view.findViewById(R.id.titleTV),
                                DetailsActivity.VIEW_NAME_HEADER_TITLE));
        ActivityCompat.startActivity(DashboardActivity.this,intent,activityOptions.toBundle());
    }

    @Override
    public void onLongClick(View view, int position) {

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
