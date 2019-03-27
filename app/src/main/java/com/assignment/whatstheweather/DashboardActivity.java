package com.assignment.whatstheweather;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.Manifest;
import android.app.Service;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;

import com.assignment.whatstheweather.Models.SoilTypes;
import com.assignment.whatstheweather.adapters.SoilAdapter;
import com.assignment.whatstheweather.utils.Constants;
import com.assignment.whatstheweather.utils.GridSpacingItemDecoration;
import com.assignment.whatstheweather.utils.ServerComm;
import com.assignment.whatstheweather.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class DashboardActivity extends AppCompatActivity implements LocationListener {

    private String latitude, longitude;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    LocationManager locationManager;
    Location loc;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    private RecyclerView recyclerView;
    private ArrayList<SoilTypes> soilList;
    private SoilAdapter adapter;


    private Toolbar toolbar;
    private String minMaxTempStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar_dashboard);
        setSupportActionBar(toolbar);

        initCollapsingToolbar();

        initView();

        initGridView();

        prepareSoilList();

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", latitude);
            jsonObject.put("lon", longitude);
            jsonObject.put("appid", Constants.TOKEN);
            jsonObject.put("units", "metric");
            String queryString = getQueryString(jsonObject);
            authenticateUser(queryString);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void prepareSoilList() {
        try{
            InputStream inputStream = DashboardActivity.this.getResources().openRawResource(R.raw.soil);
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

    /**
     * This will make the data and recycler list view connection
     */
    private void initGridView() {
        soilList = new ArrayList<>();
        adapter = new SoilAdapter(DashboardActivity.this, soilList);

        int colSpan = Utils.calculateNoOfColumns(DashboardActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, colSpan);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(colSpan, Utils.dpToPx(DashboardActivity.this,10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //TODO: add touch listener
        //recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,recyclerView,this));
    }

    /**
     * Init all Activity view
     */
    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
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
                    String titleStr = "";
                    if(!TextUtils.isEmpty(minMaxTempStr)){
                        titleStr = minMaxTempStr;
                    }
                    else{
                        titleStr = getString(R.string.app_name);
                    }
                    collapsingToolbar.setTitle(titleStr);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    public String getQueryString(JSONObject unparsedJson) throws JSONException {
        StringBuilder sb = new StringBuilder();
        Iterator<String> keys = unparsedJson.keys();
        sb.append("?"); //start of query args
        while (keys.hasNext()) {
            String key = keys.next();
            sb.append(key);
            sb.append("=");
            sb.append(unparsedJson.get(key));
            sb.append("&"); //To allow for another argument.
        }

        return sb.toString();
    }

    /**
     * Api which will check the user is already present or not
     */
    private void authenticateUser(final String requestParams) {
        try {
            new ServerComm(new ServerComm.ServerCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        Log.e("Resp Weather :: ", "" + result);

                        JSONObject weatherResponseObject = new JSONObject(result);
                        JSONObject weatherInfo = weatherResponseObject.getJSONObject("main");

                        Object minTempObj = weatherInfo.get("temp_min");
                        Object maxTempObj = weatherInfo.get("temp_max");
                        minMaxTempStr = "Min." + minTempObj + "\u2103" + " & Max." + maxTempObj + "\u2103";
                        if (getSupportActionBar() != null){
                            getSupportActionBar().setTitle(minMaxTempStr);
                        }

                        Log.e("Temperatures ", "Min : " + minTempObj + " Max : " + maxTempObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String result, int errorCode) {
                    Utils.showApiErrorMessage(DashboardActivity.this, getString(R.string.error), "");
                }
            }, DashboardActivity.this).communicateWithServerJsonParameters(Constants.REQUEST_TYPE_GET, "data/2.5/weather" + requestParams, null, true, true, false, getResources().getString(R.string.please_wait));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }


    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel(getResources().getString(R.string.permission_message),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissionsRejected.toArray(
                                                new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                    }
                                }
                            });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(DashboardActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI---->" + loc.getLatitude() + ":" + loc.getLongitude());
        latitude = String.valueOf(loc.getLatitude());
        longitude = String.valueOf(loc.getLongitude());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

}
