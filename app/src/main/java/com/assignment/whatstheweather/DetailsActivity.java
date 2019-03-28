package com.assignment.whatstheweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.assignment.whatstheweather.Models.SoilTypes;
import com.assignment.whatstheweather.adapters.SoilAdapter;
import com.assignment.whatstheweather.listeners.RecyclerTouchListener;
import com.assignment.whatstheweather.utils.GridSpacingItemDecoration;
import com.assignment.whatstheweather.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class DetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PARAM_ID = "header_url";
    public static final String EXTRA_PARAM_TITLE = "header_title";
    public static final String VIEW_NAME_HEADER_IMAGE = "selected_view_id";
    public static final String VIEW_NAME_HEADER_TITLE = "selected_view_title";

    private RecyclerView recyclerView;
    private ArrayList<SoilTypes> soilList;
    private SoilAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        initView();

        initGridView();

        prepareSoilList();


    }

    /**
     * Init all Activity view
     */
    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
    }

    /**
     * This will make the data and recycler list view connection
     */
    private void initGridView() {
        soilList = new ArrayList<>();
        adapter = new SoilAdapter(DetailsActivity.this, soilList);

        int colSpan = Utils.calculateNoOfColumns(DetailsActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, colSpan);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(colSpan, Utils.dpToPx(DetailsActivity.this,10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //TODO: add touch listener
        //recyclerView.addOnItemTouchListener(new RecyclerTouchListener(DetailsActivity.this ,recyclerView,DashboardActivity.this));
    }

    /**
     *   This prepares the SoilList from the json file.
     */
    private void prepareSoilList() {
        try{
            InputStream inputStream = DetailsActivity.this.getResources().openRawResource(R.raw.soil);
            String movieJsonStr = new Scanner(inputStream).useDelimiter("\\A").next();
            JSONArray movieJsonArr = new JSONArray(movieJsonStr);
            for(int index=0;index<movieJsonArr.length();index++){
                JSONObject soilDetails = (JSONObject) movieJsonArr.get(index);
                SoilTypes soil = new SoilTypes(
                        soilDetails.getInt("id"),
                        soilDetails.getString("title")
                );
                soilList.add(soil);
            }
        }
        catch (JSONException ex){
            ex.printStackTrace();
        }
    }

}
