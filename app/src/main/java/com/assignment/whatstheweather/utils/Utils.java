package com.assignment.whatstheweather.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.assignment.whatstheweather.DashboardActivity;
import com.assignment.whatstheweather.R;
import com.assignment.whatstheweather.interfaces.IAlertTargetHandler;

public class Utils {
    /**
     * Check if connected to the Internet
     * true for showing network is connected or not
     *
     * @param context
     * @param showFlag
     * @return
     */
    public static boolean isNetworkConnected(final Context context, boolean showFlag) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        boolean result = false;

        if (ni != null && ni.isConnectedOrConnecting()) {
            result = true;
        } else {
            if (showFlag) {
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showNetworkErrorAlertDialog(context);
                    }
                });
            }
            result = false;
        }
        return result;
    }

    /**
     * helper method for isNetworkConnected to display the error dialog.
     *
     * @param context
     */
    private static AlertDialog showNetworkErrorAlertDialog(Context context) {
        AlertDialog dialog = null;
        try {
            dialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.connection_error))
                    .setMessage(context.getString(R.string.check_internet))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                dialogInterface.dismiss();
                            }
                        }
                    })
                    .create();
            dialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dialog;
    }

    /**
     * Hide the keyboard
     *
     * @param context
     */
    public static void hideSoftKeyboard(Activity context) {
        if (context.getCurrentFocus() != null && context.getCurrentFocus() instanceof EditText) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            context.getCurrentFocus().clearFocus();

        }
    }

    /**
     * Set dialog button color according to sdk version
     *
     * @param alertDialog
     * @param ctx
     */
    public static void setDialogButtonColor(AlertDialog alertDialog, final Context ctx) {

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {                    //
                Button positiveButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setAllCaps(false);

                Button negativeButton = ((AlertDialog) dialog)
                        .getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setAllCaps(false);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Button btn;
            btn = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            if (btn != null) {
                btn.setTextColor(ctx.getColor(R.color.colorPrimary));
                btn.setAllCaps(false);
            }
            btn = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (btn != null) {
                btn.setTextColor(ctx.getColor(R.color.colorPrimary));
                btn.setAllCaps(false);
            }
            btn = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            if (btn != null) {
                btn.setTextColor(ctx.getColor(R.color.colorPrimary));
                btn.setAllCaps(false);
            }
        }
    }

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
     * Fetch color resource
     *
     * @param resId
     * @return
     */
    private int getColorFromRes(Context context, @ColorRes int resId) {
        return ContextCompat.getColor(context, resId);
    }

    public static void showConfirmDialog(final Context mContext,
                                         final String title, final String msg,
                                         final String positiveBtnCaption, final String negativeBtnCaption,
                                         final boolean isCancelable, final IAlertTargetHandler target) {

        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                int imageResource = android.R.drawable.ic_dialog_alert;
                Drawable image = mContext.getResources().getDrawable(
                        imageResource);

                builder.setTitle(title)
                        .setMessage(msg)
                        .setCancelable(false)
                        .setPositiveButton(positiveBtnCaption,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        target.onClickHandler(true);
                                    }
                                })
                        .setNegativeButton(negativeBtnCaption,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        target.onClickHandler(false);
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.setCancelable(isCancelable);
                alert.show();
                if (isCancelable) {
                    alert.setOnCancelListener(new DialogInterface.OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface arg0) {
                            target.onClickHandler(false);
                        }
                    });
                }
            }
        });


    }

    public static void showApiErrorMessage(Context context, String title, String message) {
        try {
            AlertDialog currentShownErrorDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setCancelable(false)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, null)
                    .create();
            currentShownErrorDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
