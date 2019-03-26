package com.assignment.whatstheweather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.assignment.whatstheweather.App;
import com.assignment.whatstheweather.interfaces.ServerCallback;

import org.json.JSONException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class ServerComm {
    private static final String TAG = "ServerComm";
    public ServerCallback listener = null;
    Context context;
    ProgressDialog progressDialog = null;

    public ServerComm() {
    }

    public ServerComm(Context context) {
        this.context = context;
    }


    public ServerComm(ServerCallback listener, Context context) {
        this.listener = listener;
        this.context = context;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = ( ConnectivityManager ) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if ( ni != null && ni.isConnectedOrConnecting() ) {
            return true;
        } else {
            //Toast.makeText(context.getApplicationContext(), checkInternetStatus, Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(context)
                    .setTitle("Connection error")
                    .setMessage("Please check your internet connection.")
                    .setPositiveButton(android.R.string.yes, null)
                    .create().show();
            return false;
        }
    }

    /**
     * Showing Progress Dialog For Authentication of user
     */
    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait.....");
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public void communicateWithServer(String action, final Map<String, String> params, boolean showProgress, String message) {

        //TODO : change the Base URL
        final String serverUrl = Constants.BASE_URL + action;

        Log.d("KRIXI", params.toString());

        HttpsTrustManager.allowAllSSL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response.toString();
                        if ( progressDialog != null )
                            progressDialog.dismiss();
                        try {
                            listener.onSuccess(result);
                        } catch ( JSONException e ) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if ( progressDialog != null ) {
                            progressDialog.dismiss();
                            showErrorMessage("Unable to connect", "Unable to connect to server. Please check your internet, or try after some time");
//                            Log.d(TAG, error.toString());
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 2, 2.0f));
        App.getInstance().addToRequestQueue(stringRequest);
        if ( showProgress )
            showProgressDialog(message);
    }

    public void showErrorMessage(String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .create().show();
    }

    /**
     * Granting permission to Access for website safely
     */

    public static class HttpsTrustManager implements X509TrustManager {

        private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[]{};
        private static TrustManager[] trustManagers;

        public static void allowAllSSL() {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });

            SSLContext context = null;
            if ( trustManagers == null ) {
                trustManagers = new TrustManager[]{new HttpsTrustManager()};
            }

            try {
                context = SSLContext.getInstance("TLS");
                context.init(null, trustManagers, new SecureRandom());
            } catch ( NoSuchAlgorithmException e ) {
                e.printStackTrace();
            } catch ( KeyManagementException e ) {
                e.printStackTrace();
            }

            HttpsURLConnection.setDefaultSSLSocketFactory(context
                    .getSocketFactory());
        }

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
                throws java.security.cert.CertificateException {
        }

        public boolean isClientTrusted(X509Certificate[] chain) {
            return true;
        }

        public boolean isServerTrusted(X509Certificate[] chain) {
            return true;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return _AcceptedIssuers;
        }
    } //end of HttpsTrustManager class
}//end of ServerComm class
