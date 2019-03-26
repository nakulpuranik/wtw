package com.assignment.whatstheweather.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Message;
import android.support.v7.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.assignment.whatstheweather.App;
import com.assignment.whatstheweather.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ServerComm {
    private static final String TAG = ServerComm.class.getSimpleName();
    private ServerCallback requestListener = null;
    private Context context;
    private ProgressDialog progressDialog = null;
    private AlertDialog currentShownErrorDialog = null;

    public ServerComm() {
    }

    public ServerComm(Context context) {
        this.context = context;
    }

    public ServerComm(ServerCallback listener, Context context) {
        this.requestListener = listener;
        this.context = context;
    }

    /**
     * This calls the API which accepts json as parameter and returns result.
     *
     * @param requestType
     * @param action
     * @param requestParameterJsonObj
     * @param showProgress
     * @param isFeedbackCall
     * @param message
     */
    public void communicateWithServerJsonParameters(int requestType, final String action, final JSONObject requestParameterJsonObj, boolean showProgress, boolean checkInternet, boolean isFeedbackCall, String message) {

        final String serverUrl = Constants.BASE_URL + action;

        if (checkInternet) {
            if (!Utils.isNetworkConnected(context, showProgress)) {
                //check internet flag specified and internet is not connected then return
                return;
            }
        }
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(requestType, serverUrl, requestParameterJsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String result = response.toString();
                try {
                    requestListener.onSuccess(result);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Message message = new Message();
                        message.obj = error.getMessage();
                        error.printStackTrace();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        try {
                            if ((null != error) && (null != error.networkResponse)) {
                                String errorResponse = getErrorResponseJson(error);

                                int statusCode = error.networkResponse.statusCode;
                                requestListener.onError(errorResponse, statusCode);
                            } else {
                                showErrorMessage(context.getString(R.string.error), context.getString(R.string.something_went_wrong));
                                requestListener.onError(context.getString(R.string.error), 500);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                String accessToken = Constants.TOKEN;
                if (accessToken != null && !accessToken.isEmpty()) {
                    headers.put("Content-Type", "application/json");
                    //headers.put("Authorization", "Bearer " + accessToken);
                }
                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject result = null;
                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);
                    else
                        result = new JSONObject();

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };
        jsonObjRequest.setShouldCache(false);
        if (isFeedbackCall)
            jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MAX_RETRY_POLICY_VOLLEY, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        else
            jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        App.getInstance().addToRequestQueue(jsonObjRequest);
        if (showProgress)
            showProgressDialog(message);
    }

    /**
     * This method will get the response string from the volley error object.
     *
     * @param error
     * @return
     */
    private String getErrorResponseJson(VolleyError error) {
        String errorResponse = "";
        try {
            NetworkResponse networkResponse = error.networkResponse;
            if (networkResponse != null && networkResponse.data != null) {
                errorResponse = new String(error.networkResponse.data,
                        HttpHeaderParser.parseCharset(error.networkResponse.headers, "utf-8"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return errorResponse;
    }


    /**
     * This method is responsible for handling HTTP status code and show/perform appropriate actions.
     *
     * @param statusCode
     */
    private void handleHttpStatusCodes(VolleyError error, int statusCode) {
        //Handle the http status code appropriately.
        String errorMessage = "";

        NetworkResponse networkResponse = error.networkResponse;
        String errorMsg = "";

        try {
            if (networkResponse != null && networkResponse.data != null) {
                JSONObject responseJson = new JSONObject(new String(networkResponse.data));
                errorMsg = responseJson.optJSONObject("error").optString("message");
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        Utils.showApiErrorMessage(context, context.getResources().getString(R.string.error), errorMsg);
    }

    /**
     * Showing Progress Dialog For Authentication of user
     */
    private void showProgressDialog(String message) {

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(context.getString(R.string.please_wait));
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    public void showErrorMessage(String title, String message) {
        try {
            currentShownErrorDialog = new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, null)
                    .create();
            currentShownErrorDialog.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * check parameters for null before sending it.
     *
     * @param map
     * @return
     */
    private Map<String, String> checkParams(Map<String, String> map) {
        for (Map.Entry<String, String> pairs : map.entrySet()) {
            if (pairs.getValue() == null) {
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }


    /**
     * Handles the server response delegation
     */
    public interface ServerCallback {
        void onSuccess(String result) throws JSONException;

        void onError(String result, int errorCode) throws JSONException;
    }

}
