package com.assignment.animation.animationassignment.utility;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.assignment.animation.animationassignment.details.DetailsActivity;

import java.io.InputStream;

/**
 * Created by Nakul on 8/8/18.
 */

public class Util {

    /**
     * Get the appropriate number of columns
     * @param context
     * @return noOfColumns appropriate column number
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }

    /**
     * Converting dp to pixel
     */
    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Get bitmap of the image from url.
     * @param url
     * @return
     */
    public static Bitmap getBitmapFromUrl(String url) {
        Bitmap mIcon= null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            mIcon = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon;
    }

    /**
     * check internet connectivity
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Display message via toast
     * @param detailsActivity
     * @param message
     */
    public static void showMessage(DetailsActivity detailsActivity, String message) {
        Toast.makeText(detailsActivity, message, Toast.LENGTH_LONG).show();
    }
}
